#include <SoftwareSerial.h>
#define BT_SERIAL_RX 11
#define BT_SERIAL_TX 10
int pulsePin = A0;                
int blinkPin = 13;                
volatile int BPM;                   
volatile int Signal;                
volatile int IBI = 600;              
volatile boolean Pulse = false;      
volatile boolean QS = false; 
       

static boolean serialVisual = true;   
SoftwareSerial BluetoothSerial(BT_SERIAL_RX, BT_SERIAL_TX);
 

volatile int rate[10];                     
volatile unsigned long sampleCounter = 0;          
volatile unsigned long lastBeatTime = 0;           
volatile int P = 512;                      
volatile int T = 512;                     
volatile int thresh = 525;                
volatile int amp = 100;                 
volatile boolean firstBeat = true;        
volatile boolean secondBeat = false;      

void setup()
{
  pinMode(blinkPin,OUTPUT);         
  Serial.begin(115200);   
  BluetoothSerial.begin(9600);
  pinMode(BT_SERIAL_TX, OUTPUT);             
  interruptSetup();                  
                                     
                                    
                                       
}



void loop()
{
   serialOutput();  
   
  if (QS == true) 
    {     
      
      
      serialOutputWhenBeatHappens();      
      QS = false;     
    }
     
  delay(20); 
}


void interruptSetup()
{     
  
  TCCR2A = 0x02;     
  TCCR2B = 0x06;      
  OCR2A = 0X7C;      
  TIMSK2 = 0x02;     
  sei();                   
} 

void serialOutput()
{    
 if (serialVisual == true)
  {  
     arduinoSerialMonitorVisual('-', Signal);   
  } 
 else
  {
      sendDataToSerial('S', Signal);     
   }        
}

void serialOutputWhenBeatHappens()
{    
 if (serialVisual == true) 
   {            
     Serial.print(" Heart-Beat Found ");  
     Serial.print("BPM: ");
     Serial.println(BPM);
   }
 else
   {
     sendDataToSerial('B',BPM);   
     sendDataToSerial('Q',IBI);   
   }   

 if (BPM>160)
   {
     pinMode(BT_SERIAL_TX, HIGH); 
    }
}

void arduinoSerialMonitorVisual(char symbol, int data )
{    
  const int sensorMin = 0;      
  const int sensorMax = 1024;    
  int sensorReading = data; 
  int range = map(sensorReading, sensorMin, sensorMax, 0, 11);
   
  
}


void sendDataToSerial(char symbol, int data )
{
   Serial.print(symbol);
   Serial.println(data);                
}

ISR(TIMER2_COMPA_vect) 
{  
  cli();                                      
  Signal = analogRead(pulsePin);               
  sampleCounter += 2;                         
  int N = sampleCounter - lastBeatTime;       
                                              
  if(Signal < thresh && N > (IBI/5)*3) 
    {      
      if (Signal < T) 
      {                        
        T = Signal;  
      }
    }

  if(Signal > thresh && Signal > P)
    {          
      P = Signal;                             
    }                                        



  if (N > 250)
  {                                   
    if ( (Signal > thresh) && (Pulse == false) && (N > (IBI/5)*3) )
      {        
        Pulse = true;                               
        digitalWrite(blinkPin,HIGH);                
        IBI = sampleCounter - lastBeatTime;         
        lastBeatTime = sampleCounter;               
  
        if(secondBeat)
        {                        
          secondBeat = false;                  
          for(int i=0; i<=9; i++) 
          {             
            rate[i] = IBI;                      
          }
        }
  
        if(firstBeat) 
        {                         
          firstBeat = false;                   
          secondBeat = true;                   
          sei();                               
          return;                              
        }   
      
      word runningTotal = 0;                      

      for(int i=0; i<=8; i++)
        {                
          rate[i] = rate[i+1];                   
          runningTotal += rate[i];              
        }

      rate[9] = IBI;                          
      runningTotal += rate[9];                
      runningTotal /= 10;                      
      BPM = 60000/runningTotal;               
      QS = true;                               
          }                       
  }

  if (Signal < thresh && Pulse == true)
    {   
      digitalWrite(blinkPin,LOW);            
      Pulse = false;                         
      amp = P - T;                           
      thresh = amp/2 + T;                    
      P = thresh;                            
      T = thresh;
    }

  if (N > 2500)
    {                           
      thresh = 512;                          
      P = 512;                               
      T = 512;                               
      lastBeatTime = sampleCounter;                  
      firstBeat = true;                      
      secondBeat = false;                    
    }

  sei();                                   
}
