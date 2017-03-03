/* Lectura de fuerza de un sensor FSR
 * ==================================
 * Lee los valores del sensor FSR en uno de los puertos analógicos. Estos valores los muestra en un LCD 16x2 
 * y los envía por serie para poder manejarlos desde el ordenador con la librería RXTX de Arduino para Java.
 * 
 * Autor: Jordi Castelló
 */
#include <LiquidCrystal_I2C.h>

  // Pines y constantes:
  const int fsrPin = 0;     // FSR conectado al pin A0 con un pulldown de 10K
  const int piezoPin = 6;   // Zumbador conectado a la patilla 6 (Patilla con PWM)
  const int tarButton = 7;  // Pines digitales
  const int lockButton = 8;
  // No funciona lo de abajo, al parecer es por cambiar lcd.write() por lcd.print:
  //byte padlockClose[8] =  { B01110, B10001, B10001, B10001, B11111, B11011, B11011, B01110 }; // Carácter candado cerrado
  //byte padlockOpen[8] = { B01110, B10001, B10000, B10000, B11111, B11011, B11011, B01110 }; // Carácter candado abierto
  LiquidCrystal_I2C lcd(0x3F,16,2);  // Dirección I2C: 0x3F, Columnas, Filas
  
  // Variables:  
  int tarButtonValue, lockButtonValue, fsrReading;
  boolean locked = false, tared;     // if true, screen value is locked.
  // Variables para cálculos:
  long fsrForce;       // Finally, the resistance converted to force
  long fsrForceTar = 0L;
  
  void setup() {
    Serial.begin(9600);
    pinMode(piezoPin, OUTPUT);
    pinMode(tarButton, INPUT_PULLUP);     // Botón conectado a gnd y al pin.
    pinMode(lockButton, INPUT_PULLUP);    // Tienen pull-up interno.
    //lcd.createChar(0, padlockOpen);
    //lcd.createChar(1, padlockClose);
    lcd.backlight();
    lcd.init(); // El lcd tiene 16 columnas y 2 filas.
  }
  
  void loop() {
    tarButtonValue = digitalRead(tarButton);
    lockButtonValue = digitalRead(lockButton);
    lcd.setCursor(0,0); // Para posicionar el cursor en la pantalla (columna, fila)
    lcd.print("Force in Newtons");
    lcd.setCursor(0,1); // (Columna, fila)   
    lcd.print("F: "); 
    lcd.setCursor(3,1);
    lcd.print(fsrForce);
    lcd.print(" N");
    lcd.print("     ");
    lcd.setCursor(14,1);  // Posición para el símbolo del candado   
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
   * Realiza los cálculos de fuerza según el valor analógico de entrada.
   */
  void fsrCalc() {
    // Código sacado de la página de Adafruit, tutorial de FSR (ligeramente modificado).   
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
   * Para tarar, toma el valor actual como referencia de 0. Guarda el valor de la fuerza
   * actual para posteriormente restarlo a la fuerza que tengamos.
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
