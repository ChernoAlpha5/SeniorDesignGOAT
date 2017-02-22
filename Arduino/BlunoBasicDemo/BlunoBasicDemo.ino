void setup() {
    DDRD = DDRD | B11111100;  //set PORTD (digital 2 - 6) to output
    Serial.begin(115200);               //initial the Serial
}


void loop()
{
    for (int i =100000; i < 100070; i++){
      PORTD ^= 0x8; //toggle PD3    
      PORTD ^= 0x8; //toggle PD3
      Serial.print(i); 
      PORTD ^= 0x8; //toggle 
      //Serial.print("\n"); 
      //delay(150);
    }
    //Serial.write("=======\n");
    delay(500);
    /*if(Serial.available())
    {
        Serial.write(Serial.read());    //send what has been received
    }*/
}


