/*
 * robotSocket.java
 *
 * Created on June 26, 2014, 10:12 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package DrRobot.JaguarControl;
import java.io.*;
import java.net.*;
/**
 *
 * @author Dr Robot Inc
 */
public class robotSocket implements Runnable {
    private Socket sock = null;
    private String robotip;
    private int robotport;
    private DataOutputStream out = null;
    private InputStream in = null;
    private BufferedReader reader = null;
    public boolean sendFlag = true;
    public boolean recFinished = false;
    private int recCnt  =0;

    private String strCommand = "";
    private boolean runFlag = false;
    public int []driverState = {0,0,0,0};
    public double []drvVoltage = {0.0,0.0,0.0,0.0};
    public double []batVoltage = {0.0,0.0,0.0,0.0};
    public double []reg5Voltage = {0.0,0.0,0.0,0.0};
            
    public double []ch1Temp = {0.0,0.0,0.0,0.0};
    public double []ch2Temp = {0.0,0.0,0.0,0.0};
    public int []EncoderPos = {0,0,0,0,0,0,0,0};
    public int []EncoderSpeed = {0,0,0,0,0,0,0,0};
    public int []MotorPower = {0,0,0,0,0,0,0,0};
    public double []MotorTemp = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
    public double []MotorAmp = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
    public int []AccelRaw = {0,0,0};
    public int []GyroRaw = {0,0,0};
    public int []CompassRaw = {0,0,0};
    public int imuSeq = 0;
    public double estYaw = 0;
    public double GPSLat = 0;
    public double GPSLong = 0;
    public double GPSCog = 0;
    public double GPSVog = 0;
    public int GPSState = 0;    // 0 -- invalid, 1 -- valid
    public double GPSTimeStamp = 0;
    //custom sensor data
    public int []customAD = {0,0,0,0,0,0,0,0};
    public int customIO = 0;

    
    //for temperature sensor
    final double[] resTable = {114660,84510,62927,47077,35563,27119,20860,16204,12683,10000,
                    7942,6327,5074,4103,3336,2724,2237,1846,1530,1275,1068,899.3,760.7,645.2,549.4};
    final double[] tempTable = { -20, -15, -10, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100 };
    final double FULLAD = 4095;
    final double KNNOT2MS = 0.514444444;
    final double R2D = 180 / Math.PI;
    
    /** Creates a new instance of robotSocket
     * @param robotIP  main robot control network module IP address
     * @param robotPort main robot control network module port number*/
    public robotSocket(String robotIP, int robotPort) {
    
        
        this.robotip = robotIP;
        this.robotport = robotPort;
               
        //socket connect
        try {
	    
            sock = new Socket(robotip,robotport);
	} catch(IOException e) {

	    System.exit(3);
           return;
	}
   
        
        
        try {
            out = new DataOutputStream(sock.getOutputStream());
        }catch (IOException e){
        }
	try {
            out.writeBytes("PING\r\n" );    
            sendFlag = true;
            recCnt = 0;
        }catch (IOException e){
        }
         
        try {
                in = sock.getInputStream();
            } catch(IOException e) {
                // System.exit(5);
                return;
            }
        reader = new BufferedReader(new InputStreamReader(in));
        runFlag = true;
  
    }
    

    
   public void run(){
     boolean res;
     String strRev = "";
       while(runFlag)
       {
           res = false;
           try {
                res = reader.ready();    
            }catch(IOException e){
            }

           if (res) 
            {    //reader.ready()
                try {
                    strRev = reader.readLine();
                }catch(IOException e){
                }
                        
                System.out.println ("Receive Data =  " + strRev);
                DealWithPackage(strRev);
            }
       }//run flag
   }
   
   private void DealWithPackage(String strRev){
       int index = 0;
        if ( strRev.startsWith("#") ){   
            //IMU sensor package
            strRev = strRev.substring(1);   //remove "#"
            String []data = strRev.split(",");
            if (data.length > 15){

                try{

                    imuSeq = Integer.parseInt(data[0]);
                    estYaw = Double.parseDouble(data[2]);
                    GyroRaw[0] = Integer.parseInt(data[4]);
                    GyroRaw[1] = Integer.parseInt(data[5]);
                    GyroRaw[2] = Integer.parseInt(data[6]);
                    AccelRaw[0] = Integer.parseInt(data[8]);
                    AccelRaw[1] = Integer.parseInt(data[9]);
                    AccelRaw[2] = Integer.parseInt(data[10]);
                    CompassRaw[0] = Integer.parseInt(data[12]);
                    CompassRaw[1] = Integer.parseInt(data[13]);
                    CompassRaw[2] = Integer.parseInt(data[14]);
                }
                catch(NumberFormatException e){
                 }
            }
                    
        }
        else if(strRev.startsWith("$GPRMC")){
            // GPS sensor package
            String []data = strRev.split(",");
            if (data.length > 9){
                GPSTimeStamp = Double.parseDouble(data[1]);

                try{
                    if ("A".equals(data[2])){
                        GPSState = 1;
                    }
                    else if("V".equals(data[2])){
                        GPSState = 0;
                    }
                    GPSLat = Double.parseDouble(data[3]);
                    if ("S".equals(data[4])){
                        GPSLat = - GPSLat;
                    }
                    GPSLong = Double.parseDouble(data[5]);
                    if ("W".equals(data[6])){
                        GPSLong = - GPSLong;
                    }
                    if (!data[7].isEmpty()){
                        GPSVog = Double.parseDouble(data[7]) * KNNOT2MS;    
                    }
                    if (!data[8].isEmpty()){
                        GPSCog = Double.parseDouble(data[8]);
                    }
                }
                catch(NumberFormatException e){

                }
            }

        }

        else if(strRev.startsWith("MM")){
            // motor/motordriver package
            
            if (strRev.startsWith("MM0")){
                index = 0;
            }
            else if (strRev.startsWith("MM1")){
                index = 1;
            }
            else if (strRev.startsWith("MM2")){
                index = 2;
            }
            else if (strRev.startsWith("MM3")){
                index = 3;
            }
                //driver 1 and front motors
                strRev = strRev .substring(4);
                try{
                    
                    if (strRev.startsWith("A=")){
                        //current data
                        strRev = strRev.substring(2);
                        String []data = strRev.split(":");
                        MotorAmp[index * 2 + 0] = Double.parseDouble(data[0])/10;
                        MotorAmp[index * 2 + 1] = Double.parseDouble(data[1])/10;
                    }
                    else if(strRev.startsWith("AI=")){
                        // A/D data, here 3,4 will be motor temperature sensor
                        strRev = strRev.substring(3);
                        String []data = strRev.split(":");
                        MotorTemp[index * 2 + 0] = AD2Temperature(Integer.parseInt(data[2]));
                        MotorTemp[index * 2 + 1] = AD2Temperature(Integer.parseInt(data[3]));
                    }
                    else if(strRev.startsWith("C=")){
                        // encoder position data
                        strRev = strRev.substring(2);
                        String []data = strRev.split(":");
                        EncoderPos[index * 2 + 0] = Integer.parseInt(data[0]);
                        EncoderPos[index * 2 + 1] = Integer.parseInt(data[1]);
                    }
                    else if(strRev.startsWith("P=")){
                        // output PWM value, 0 ~ 1000
                        strRev = strRev.substring(2);
                        String []data = strRev.split(":");
                        MotorPower[index * 2 + 0] = Integer.parseInt(data[0]);
                        MotorPower[index * 2 + 1] = Integer.parseInt(data[1]);

                    }
                    else if(strRev.startsWith("S=")){
                        // encoder velocity data RPM
                        strRev = strRev.substring(2);
                        String []data = strRev.split(":");
                        EncoderSpeed[index * 2 + 0] = Integer.parseInt(data[0]);
                        EncoderSpeed[index * 2 + 1] = Integer.parseInt(data[1]);

                    }
                    else if(strRev.startsWith("T=")){
                        // motor driver board temperature
                        strRev = strRev.substring(2);
                        String []data = strRev.split(":");
                        ch1Temp[index] = Double.parseDouble(data[0]);
                        ch2Temp[index] = Double.parseDouble(data[1]);
                    }
                    else if(strRev.startsWith("V=")){
                        // voltage data
                        strRev = strRev.substring(2);
                        String []data = strRev.split(":");
                        drvVoltage[index] = Double.parseDouble(data[0])/10;
                        batVoltage[index] = Double.parseDouble(data[1])/10;
                        reg5Voltage[index] = Double.parseDouble(data[2]) /1000;

                    }
                    else if(strRev.startsWith("CR=")){
                        // here is the encoder relative difference reading, 
                        // very useful to estimate the encoder/motor traveling distance
                    }
                    else if(strRev.startsWith("FF=")){
                        // driver board state
                        strRev = strRev.substring(3);
                        driverState[index] = Integer.parseInt(strRev);
                    }
                }
                catch(NumberFormatException e){
                    
                }
          
        }
                        
}

   
      public void sendCommand(String cmd){
          String sendCmd = cmd +"\r\n";
          if (sendFlag){
                try {
                    out.writeBytes(sendCmd );    
                }catch (IOException e){

                }
          }
    }
   
      private double AD2Temperature(int adValue){
         double tempM;
            double k = (adValue / FULLAD);
            double resValue;
            if (k != 1)
            {
                resValue = 10000 * k / (1 - k);      //AD value to resistor
            }
            else
            {
                resValue = resTable[0];
            }


            int index = -1;
            if (resValue >= resTable[0])       //too lower
            {
                tempM = -20;
            }
            else if (resValue <= resTable[24])
            {
                tempM = 100;
            }
            else
            {
                for (int i = 0; i < 24; i++)
                {
                    if ((resValue <= resTable[i]) && (resValue >= resTable[i + 1]))
                    {
                        index = i;
                        break;
                    }
                }
                if (index >= 0)
                {
                    tempM = tempTable[index] + (resValue - resTable[index]) / (resTable[index + 1] - resTable[index]) * (tempTable[index + 1] - tempTable[index]);
                }
                else
                {
                    tempM = 0;
                }

            }

            return tempM;
      }
      

   
}
