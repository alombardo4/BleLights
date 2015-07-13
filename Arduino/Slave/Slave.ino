

/**
 * Master of master slave BLE system
 * Version 0.0.1
 * Features in place: 
 *    - BLE String read
 */

//"RBL_nRF8001.h/spi.h/boards.h" is needed in every new project
#include <SPI.h>
#include <EEPROM.h>
#include <boards.h>
#include <Adafruit_NeoPixel.h>
#define NUMPIXELS 5
#define NEOPIXEL_PIN 5

#include "RF24.h"
Adafruit_NeoPixel strip = Adafruit_NeoPixel(5, 5, NEO_RGB + NEO_KHZ800);
// 0 R G B Program SlaveID Brightness
byte message[32];

RF24 radio(8,9);
// Radio pipe addresses for the 2 nodes to communicate.
const uint64_t pipes[2] = { 0xF0F0F0F0E1LL, 0xF0F0F0F0D2LL };

void setup()
{  
  // Enable serial debug
  Serial.begin(57600);
  
  resetArrays();

  //init strip
  strip.begin();
  updateColor(100,100,100,100);
  delay(500);
  updateColor(0,0,0,0);
  Serial.println("Starting ble");


  radio.begin();                           // Setup and configure rf radio
  
  radio.openReadingPipe(1,pipes[1]);

  radio.startListening();                 // Start listening

}

unsigned char buf[16] = {0};
unsigned char len = 0;


void loop()
{
  while(radio.available()) {
    radio.read(&message, 32);

  

    updateColor(message[1], message[2], message[3], message[6]);
    
    Serial.print(message[1]);
    Serial.print(' ');
    Serial.print(message[2]);
    Serial.print(' ');
    Serial.print(message[3]);
    Serial.println();
  }
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
  for (int i = 0; i < 32; i++) {
    message[i] = 255;
    
  }
}

