
/**
 * Master of master slave BLE system
 * Version 0.0.1
 * Features in place: 
 *    - BLE String read
 */

//"RBL_nRF8001.h/spi.h/boards.h" is needed in every new project
#include <SPI.h>
#include <nRF24L01.h>
#include <RF24.h>
#include <RF24_config.h>
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


// Radio pipe addresses for the 2 nodes to communicate.
RF24 radio(9,10);
const uint64_t pipe = 0xE8E8F0F0E1LL;
byte ids[10] = {1,0,0,0,0,0,0,0,0,0};

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


 radio.begin();
 radio.openWritingPipe(pipe);
 radio.powerUp();
  
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
    byte sender[7];
    for (int j = 0; j < 7; j++) {
      sender[j] = (byte) message[j];
    }
    while(!radio.write(sender, 7)) {
      Serial.println("Error writing message");
    }
    for (int j = 0; j < 7; j++) {
      Serial.print(message[j]);
      Serial.print(" ");
    }
    Serial.print("\n");

    if (isInGroup(message[5])) {
      for (int j = 0; j < 7; j++) {
        currentSettings[j] = message[j];
      }
      Serial.println("UPDATING");
      updateColor(currentSettings[1], currentSettings[2], currentSettings[3], currentSettings[6]);
      resetArrays();
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
boolean isInGroup(byte id) {
  for (int i = 0; i < 10; i++) {
    if (id == ids[i])
      return true;
  }
  return false;
}
