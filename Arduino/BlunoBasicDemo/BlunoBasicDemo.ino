void setup() {
    Serial.begin(115200);               //initial the Serial
}


void loop()
{
    for (int i =0; i < 10; i++){
      Serial.write("Cookies\n"); 
    }
    Serial.write("=======\n");
    delay(1500);
    if(Serial.available())
    {
        Serial.write(Serial.read());    //send what has been received
    }
}


