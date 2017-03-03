/* Force reading with FSR
 * ==================================
 * Read values from FSR in the A0 pin. This values are displayed in a 16x2 LCD Screen 
 * and data is send through serial port in JSON format. 
 * 
 * Author: Jordi Castelló
 */
#include <LiquidCrystal_I2C.h>

  // Pines y constantes:
  const int fsrPin = 0;     // FSR connected to A0 pin with 10K pulldown resistor
  const int piezoPin = 6;   // Beeper connected to pin number 6 (this pin can implement PWM)
  const int tarButton = 7;  // Digital pins
  const int lockButton = 8;
  // It Doesn't work, apparently it is for changing lcd.write() by lcd.print():
  //byte padlockClose[8] =  { B01110, B10001, B10001, B10001, B11111, B11011, B11011, B01110 }; // Locked Symbol
  //byte padlockOpen[8] = { B01110, B10001, B10000, B10000, B11111, B11011, B11011, B01110 };   // Unlocked Symbol
  LiquidCrystal_I2C lcd(0x3F,16,2);  // I2C address: 0x3F, Columns, Rows
  
  // Variables:  
  int tarButtonValue, lockButtonValue, fsrReading;
  boolean locked = false, tared;     // if true, screen value is locked.
  // Calculation variables:
  long fsrForce;       // Finally, the resistance converted to force
  long fsrForceTar = 0L;
  
  void setup() {
    Serial.begin(9600);
    pinMode(piezoPin, OUTPUT);
    pinMode(tarButton, INPUT_PULLUP);     // Buttons connected between GND & pin.
    pinMode(lockButton, INPUT_PULLUP);    // Internal pull-up configuration.
    //lcd.createChar(0, padlockOpen);
    //lcd.createChar(1, padlockClose);
    lcd.backlight();
    lcd.init();
  }
  
  void loop() {
    tarButtonValue = digitalRead(tarButton);
    lockButtonValue = digitalRead(lockButton);
    lcd.setCursor(0,0); // Set cursor position at screen (column, row)
    lcd.print("Force in Newtons");
    lcd.setCursor(0,1); // (column, row)  
    lcd.print("F: "); 
    lcd.setCursor(3,1);
    lcd.print(fsrForce);
    lcd.print(" N");
    lcd.print("     ");
    lcd.setCursor(14,1);   // Lock symbol position
    if(lockButtonValue == LOW && locked == true){
      waitKeyUp(lockButton);
      locked = false;    
    }
    else if(lockButtonValue == LOW && locked == false){
      waitKeyUp(lockButton);
      locked = true;         
    }
    if(!locked){
      fsrCalc();
      //lcd.print(byte(0));
      lcd.print(" ");
    }
    else {
      //lcd.print(byte(1));
      lcd.print("F"); // Fixed value
    }  
    if(tarButtonValue == LOW && tared){
      waitKeyUp(tarButton);
      fsrForceTar = 0L;
      tared = false;
    }
    else if(tarButtonValue == LOW && !tared) {
      waitKeyUp(tarButton);
      setTare();
      tared = true;    
      
    }
    if(lockButtonValue == LOW || tarButtonValue == LOW){
      tone(piezoPin, 3000, 50); // Beep (pin, frecuency <hz>, duration <ms>)    
    } 
    if(tared) lcd.print("T");
    else lcd.print(" "); 
    // We send values with JSON format:
    Serial.print("{\"force\":");
    Serial.print(fsrForce);
    Serial.print(",\"raw\":");
    Serial.print(fsrReading);
    Serial.println("}");
    delay(150);
  }

  /**
   * Do force calculations with the analog pin reads.
   */
  void fsrCalc() {
    // Code from Adafruit FSR (a bit modified)
    int fsrVoltage;     // the analog reading converted to voltage
    unsigned long fsrResistance;  // The voltage converted to resistance, can be very big so make "long"
    unsigned long fsrConductance;    
    fsrReading = analogRead(fsrPin);     
    // analog voltage reading ranges from about 0 to 1023 which maps to 0V to 5V (= 5000mV)
    fsrVoltage = map(fsrReading, 0, 1023, 0, 5000);   
    if (fsrVoltage == 0) {
      fsrForce = 0;
    }
    else {
      // The voltage = Vcc * R / (R + FSR) where R = 10K and Vcc = 5V
      // so FSR = ((Vcc - V) * R) / V        yay math!
      fsrResistance = 5000 - fsrVoltage;     // fsrVoltage is in millivolts so 5V = 5000mV
      fsrResistance *= 10000;                // 10K resistor
      fsrResistance /= fsrVoltage;
      fsrConductance = 1000000;           // we measure in micromhos so 
      fsrConductance /= fsrResistance;
    
      // Use the two FSR guide graphs to approximate the force
      if (fsrConductance <= 1000) {
        fsrForce = fsrConductance / 80;   
      } 
      else {
        fsrForce = fsrConductance - 1000;
        fsrForce /= 30;         
      }
      fsrForce = fsrForce - fsrForceTar;
    }
  }
  
  /**
   * Set the current force value as if it were the actual force value.
   */
  void setTare() {
    fsrCalc();
    fsrForceTar = fsrForce;
  }

  /**
   * Wait here until we release pressed button.
   */
  void waitKeyUp(int button) {
    delay(10);
    while(digitalRead(button) == LOW) {
      delay(10);
    }
  }
