/**
 * Slave of master slave BLE system
 * Version 0.0.1
 * Features in place: 
 *    - BLE String read
 */

#include <SPI.h>
#include <nRF24L01.h>
#include <RF24.h>
#include <RF24_config.h>
#include <EEPROM.h>
#include <boards.h>
#include <Adafruit_NeoPixel.h>
#define NUMPIXELS 5
#define NEOPIXEL_PIN 5

byte ids1[10] = {1,0,0,0,0,0,0,0,0,0};
byte ids2[10] = {2,0,0,0,0,0,0,0,0,0};
byte ids3[10] = {3,0,0,0,0,0,0,0,0,0};
byte ids4[10] = {4,0,0,0,0,0,0,0,0,0};
byte ids5[10] = {5,0,0,0,0,0,0,0,0,0};

Adafruit_NeoPixel strip = Adafruit_NeoPixel(5, 5, NEO_RGB + NEO_KHZ800);
// 0 R G B Program SlaveID Brightness
byte msg[7];

RF24 radio(9,10);
const uint64_t pipe = 0xE8E8F0F0E1LL;



void setup()
{  
  // Enable serial debug
  Serial.begin(57600);


  //init strip
  strip.begin();
  updateColor(100,100,100,100);
  delay(500);
  updateColor(0,0,0,0);

  //start radio
  radio.begin();
  radio.openReadingPipe(1,pipe);
  radio.powerUp();
  radio.startListening();
 
  Serial.println("Radio listening");

}


void loop()
{
  if (radio.available()) {
    bool done = false;
    while(!done) {
      done = radio.read(msg, 7);
      if (isInGroup(msg[5])) {
        updateColor((int)msg[1], (int)msg[2], (int)msg[3], (int)msg[6]);
      }
      
      for (int i = 0; i < 7; i++) {
        Serial.print(msg[i]);
        Serial.print(' ');
      }
      Serial.println();
      delay(10);
    }

  } else {

  }

}

boolean isInGroup(byte id) {
  for (int i = 0; i < 10; i++) {
    if (id == ids[i])
      return true;
  }
  return false;
}

void updateColor(int r, int g, int b, int brightness) {
  r = (int) (((double) brightness/100) * r);
  g = (int) (((double) brightness/100) * g);
  b = (int) (((double) brightness/100) * b);

  r = map(r, 0, 100, 0, 255);
  g = map(g, 0, 100, 0, 255);
  b = map(b, 0, 100, 0, 255);
  Serial.println("Setting colors: ");
  Serial.print(r);
  Serial.print(' ');
  Serial.print(g);
  Serial.print(' ');
  Serial.print(b);
  Serial.println();
  for(int i = 0; i < NUMPIXELS; i++) {
    strip.setPixelColor(i, strip.Color(g,r,b));
  }
  strip.show();
}
void resetArrays() {
  for (int i = 0; i < 7; i++) {
    msg[i] = 255;
    
  }
}

