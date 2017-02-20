void setup() {
    Serial.begin(115200);               //initial the Serial
}


void loop()
{
    for (int i =0; i < 70; i++){
      Serial.print(i); 
      //Serial.print("\n"); 
      delay(150);
    }
    //Serial.write("=======\n");
    delay(500);
    /*if(Serial.available())
    {
        Serial.write(Serial.read());    //send what has been received
    }*/
}


