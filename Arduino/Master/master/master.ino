
#include "RF24.h"





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
#include <RBL_nRF8001.h>
#include <Adafruit_NeoPixel.h>
#define NUMPIXELS 5
#define NEOPIXEL_PIN 5

Adafruit_NeoPixel strip = Adafruit_NeoPixel(5, 5, NEO_RGB + NEO_KHZ800);
// 0 R G B Program SlaveID Brightness
byte message[32];

// same as message
byte currentSettings[32];

RF24 radio(8,9);
// Radio pipe addresses for the 2 nodes to communicate.
const uint64_t pipes[2] = { 0xF0F0F0F0E1LL, 0xF0F0F0F0D2LL };

void setup()
{  
  // Enable serial debug
  Serial.begin(57600);
  
  //BLE pin setup. Running on 7 and 6 to avoid conflict with NRF24 RF Chip
  ble_set_pins(7, 6);
  
  // BLE broadcast name
  ble_set_name("BLE Lights");
  
  // Init. and start BLE library.
  ble_begin();

  resetArrays();

  //init strip
  strip.begin();
  updateColor(100,100,100,100);
  delay(500);
  updateColor(0,0,0,0);
  Serial.println("Starting ble");


  radio.begin();                           // Setup and configure rf radio
  radio.openWritingPipe(pipes[1]);
  
  
}

unsigned char buf[16] = {0};
unsigned char len = 0;


void loop()
{
  //listening for BLE input
  if ( ble_available() )
  {
    int i = 0;
    while ( ble_available() ) {
      if (i < 7) {
        message[i] = (byte) ble_read();
      }
      i++;
    }

    for (int j = 0; j < 7; j++) {
      Serial.print(message[j]);
      Serial.print(" ");
    }
    Serial.print("\n");
      
    if (message[5] == 0 || message[5] == 1) {
      for (int j = 0; j < 7; j++) {
        currentSettings[j] = message[j];
      }
      Serial.println("UPDATING");
      updateColor(currentSettings[1], currentSettings[2], currentSettings[3], currentSettings[6]);
      resetArrays();
    }
    radio.stopListening();
    if(radio.write(&message, 32)) {
      Serial.println("WROTE");
      delay(1000);
    } else {
      Serial.println("FAILED WRITE");
    }

    
  }


  
  if ( Serial.available() )
  {
    delay(5);
    
    while ( Serial.available() )
        ble_write( Serial.read() );
  }
  
  ble_do_events();
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
    message[i] = (byte) 255;
    currentSettings[i] = (byte) 255;
  }
}

