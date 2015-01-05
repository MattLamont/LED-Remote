


#define REDPIN 5
#define GREENPIN 6
#define BLUEPIN 3
#define BRIGHTNESSPIN 9
#define POWERPIN 2
#define BUZZERPIN 7

char buffer[1];
bool powerOn = true;
int num_tones = 14;
int tones[] = {261, 277, 294, 311, 330, 349, 370, 392, 415, 440 , 456 , 472 , 496 , 512 };

void setup()
{
  
  pinMode( REDPIN , OUTPUT );
  pinMode( GREENPIN , OUTPUT );
  pinMode( BLUEPIN , OUTPUT );
  pinMode( BRIGHTNESSPIN , OUTPUT );
  pinMode( POWERPIN , OUTPUT );
  
  analogWrite( REDPIN , 0 );
  analogWrite( GREENPIN , 0 );
  analogWrite( BLUEPIN , 0 );
 
  Serial.begin( 9600 ); 
}

void loop()
{
  if( Serial.available() == 4 )
  {
    delay( 200 );
    Serial.readBytes( buffer , 4 );
    updateLeds( buffer );
  } 
}

void updateLeds( char* buffer )
{
  String bufferString( buffer );
  String temp = "";
  
  char charBuffer[1];
  int colorNum = 0;
  int colorVal = 0;
  
  charBuffer[0] = bufferString[ 0 ];
  temp = charBuffer;
  colorNum = temp.toInt();
  temp = bufferString.substring( 1 );
  colorVal = temp.toInt();
  
  if( colorNum < 0 || colorNum > 5 )
  {
    Serial.println( "ERROR: Invalid LED Number" );
    return; 
  }
  
  if( colorVal < 0 || colorVal > 255 )
  {
    Serial.println( "ERROR: Invalid Color Value" );
    return; 
  }
  
  if( colorNum == 0 )
  {
    analogWrite( REDPIN , colorVal );
    Serial.println( "(ARDUINO) Red Changed to " );
    Serial.println( colorVal );
    return;
  }
  
  if( colorNum == 1 )
  {
    analogWrite( GREENPIN , colorVal );
    Serial.println( "(ARDUINO) Green Changed to " );
    Serial.println( colorVal );
    return;
  }
  
  if( colorNum == 2 )
  {
    analogWrite( BLUEPIN , colorVal );
    Serial.println( "(ARDUINO) Blue Changed to " );
    Serial.println( colorVal );
    return;
  }
  
  if( colorNum == 3 )
  {
    analogWrite( BRIGHTNESSPIN , colorVal );
    Serial.println( "(ARDUINO) Brightness Changed to " );
    Serial.println( colorVal );
    return;
  }
  
  if( colorNum == 4 )
  {
    if( powerOn == true )
    {
      digitalWrite( POWERPIN , LOW );
      Serial.println( "(ARDUINO) Led Lights Turned Off" );
      powerOn = false;
      return;  
    }
    else if( powerOn == false )
    {
      digitalWrite( POWERPIN , HIGH );
      Serial.println( "(ARDUINO) Led Lights Turned On" );
      powerOn = true;
      return;
    }  
  }
  
  if( colorNum == 5 )
  {
    playMelody();
    Serial.println( "(ARDUINO) Playing Melody..." );
    return;  
  }
  
  return;
}

void playMelody()
{
  for( int i = 0 ; i < num_tones ; i++ )
  {
    tone( BUZZERPIN , tones[i] );
    delay( 100 ); 
  }
  noTone( BUZZERPIN );
}
  
