/*
 * JaguarControlDemo.java
 *
 * Created on July 22, 2012, 12:59 AM
*/

package DrRobot.JaguarControl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.Timer;
import java.text.*;
/**
 *
 * @author  Dr Robot Inc
 */
public class JaguarControlDemo extends javax.swing.JFrame {
    private static Timer timer = null;
    private ActionListener actionListener;
   
    private Timer armTimer = null;
    private ActionListener actionListenerArm;
    private static int armCtrlCnt = 0;
    private int armCtrlChannel = 0;
    private int armCmd = 0;
    final int ARMVEL1 = 200;
    final int ARMVEL2 = 1000;
    
    private int armPositionCnt1 = 0;
    private int armPositionCnt2 = 0;
    
    private static int DisplayCnt = 0;
    private static robotSocket sock = null;
    private String robotIP ="";
    private int robotPort =0;
   
    private boolean ConnectFlag = false;
    private int motDir = 1;
    
    public static UnitySub u;
    
    /** Creates new form X80ControlDemo */
    //here is some import const refere protocol
    final byte STX0 = 94;
    final byte STX1 = 2;
    final byte ETX0 = 94;
    final byte ETX1 = 13;
    
    final byte PWMMOVE = 6;         //referee protocol, open loop PWM control
    final byte SPEEDMOVE = 27;      //referee protocol, velocity control
    
    
    final byte PINGCMD = (byte)255;
    final byte DCMOTORPARAMETERSET = 7; //

    final byte DCMOTORCTRLMODE = 14;    //
    
    final byte DCPOSITIONPID = 7;      //positon PID Control
    final byte DCVELOCITYPID = 8;      //velocity PID Control
    
    final byte PWMCTRL = 0;
    final byte POSITIONCTRL = 1;
    final byte VELOCITYCTRL = 2;
    
    final byte KpID = 1;
    final byte KdID = 2;
    final byte KiID = 3;
    
    final byte CUSTOMGPIODOUT = 22;     //referee protocol, set custom GPIO out
    final byte DCMOTORPOSITION = 3;     //referee protocol, specified DC motor Position control
    final byte ALLDCMOTORPOSITION = 4;     //all DC motor positon control
    final byte DCMOTORPWM = 5;     //referee protocol, specified DC motor PWM control
    final byte ALLDCMOTORPWM = 6;     //all DC motor PWM control
    final byte DCMOTORVELOCITY = 26;     //referee protocol,specified DC motor Velocity control
    final byte ALLDCMOTORVELOCITY = 27;     //all DC motor positon control
    
    
    final int NONCTRLCMD = 0xffff;      //no ctrl command
    final int INIPWM = 16384;
    //some robot parameters, depend on what kind of robot you rae using, here is for X80, if you have some questions,please send e-mail to info@drrobot.com
    final double WheelDis = 0.265;      //wheel distance
    final double WheelR = 0.0825;       //wheel radius
    final int CircleCnt = 190;     //encoder one circle count
    DecimalFormat df = new DecimalFormat("#.##");

    public JaguarControlDemo() {
      initComponents();
      
      
        
      actionListener = new ActionListener() {
                     public void actionPerformed(ActionEvent actionEvent) {
                            //display sensor data here
                         DisplayCnt++;
                         
                         if (ConnectFlag == true){
                            //first display motor sensor
                            jLabel_Motor1Pos.setText(Integer.toString(sock.EncoderPos[0]));
                            jLabel_Motor2Pos.setText(Integer.toString(sock.EncoderPos[1]));
                            jLabel_Motor3Pos.setText(Integer.toString(sock.EncoderPos[2]));
                            jLabel_Motor4Pos.setText(Integer.toString(sock.EncoderPos[3]));
                            jLabel_Motor1Speed.setText(Integer.toString(sock.EncoderSpeed[0]));
                            jLabel_Motor2Speed.setText(Integer.toString(sock.EncoderSpeed[1]));
                            jLabel_Motor3Speed.setText(Integer.toString(sock.EncoderSpeed[2]));
                            jLabel_Motor4Speed.setText(Integer.toString(sock.EncoderSpeed[3]));
                            
                            jLabel_Motor1Power.setText(Integer.toString(sock.MotorPower[0]));
                            jLabel_Motor2Power.setText(Integer.toString(sock.MotorPower[1]));
                            jLabel_Motor3Power.setText(Integer.toString(sock.MotorPower[2]));
                            jLabel_Motor4Power.setText(Integer.toString(sock.MotorPower[3]));
                            
                            jLabel_Motor1Temp.setText(df.format(sock.MotorTemp[0]));
                            jLabel_Motor2Temp.setText(df.format(sock.MotorTemp[1]));
                            jLabel_Motor3Temp.setText(df.format(sock.MotorTemp[2]));
                            jLabel_Motor4Temp.setText(df.format(sock.MotorTemp[3]));
                            jLabel_Motor1Amp.setText(df.format(sock.MotorAmp[0]));
                            jLabel_Motor2Amp.setText(df.format(sock.MotorAmp[1]));
                            jLabel_Motor3Amp.setText(df.format(sock.MotorAmp[2]));
                            jLabel_Motor4Amp.setText(df.format(sock.MotorAmp[3]));
                            //Board Vol
                            jLabel_BatteryVol.setText(df.format(sock.batVoltage[0])+ "V");

                            String errorStr = "";
                            int data = sock.driverState[0];
                            if ((data & 0x1) != 0)
                            {
                                errorStr = "OH";
                            }
                            else
                            {
                            }
                            if ((data & 0x2) != 0)
                            {
                                errorStr += "+OV";
                            }
                            else
                            {
                            }
                            if ((data & 0x4) != 0)
                            {
                                errorStr += "+UV";
                            }
                            else
                            {
                            }
                            if ((data & 0x8) != 0)
                            {
                                errorStr += "SHT";

                            }
                            else
                            {

                            }
                            if ((data & 0x10) != 0)
                            {
                                errorStr += "+ESTOP";
                            }
                            else
                            {

                            }
                            if ((data & 0x20) != 0)
                            {
                                errorStr += "SEPF";
                            }
                            else
                            {
                            }
                            if ((data & 0x40) != 0)
                            {
                                errorStr += "+PromF";
                            }
                            else
                            {
                            }
                            if ((data & 0x80) != 0)
                            {
                                errorStr += "+ConfF";
                            }
                            else
                            {

                            }
                            jLabel_Driver1State.setText(errorStr);
                            String errorStr1 = "";
                            data = sock.driverState[0];
                            if ((data & 0x1) != 0)
                            {
                                errorStr1 = "OH";
                            }
                            else
                            {
                            }
                            if ((data & 0x2) != 0)
                            {
                                errorStr1 += "+OV";
                            }
                            else
                            {
                            }
                            if ((data & 0x4) != 0)
                            {
                                errorStr1 += "+UV";
                            }
                            else
                            {
                            }
                            if ((data & 0x8) != 0)
                            {
                                errorStr1 += "SHT";

                            }
                            else
                            {

                            }
                            if ((data & 0x10) != 0)
                            {
                                errorStr1 += "+ESTOP";
                            }
                            else
                            {

                            }
                            if ((data & 0x20) != 0)
                            {
                                errorStr1 += "SEPF";
                            }
                            else
                            {
                            }
                            if ((data & 0x40) != 0)
                            {
                                errorStr1 += "+PromF";
                            }
                            else
                            {
                            }
                            if ((data & 0x80) != 0)
                            {
                                errorStr1 += "+ConfF";
                            }
                            else
                            {

                            }
                            jLabel_Driver2State.setText(errorStr1);
                            
                            //IMU sensor
                            jLabel_GyroX.setText(Integer.toString(sock.GyroRaw[0]));
                            jLabel_GyroY.setText(Integer.toString(sock.GyroRaw[1]));
                            jLabel_GyroZ.setText(Integer.toString(sock.GyroRaw[2]));
                            jLabel_AccelX.setText(Integer.toString(sock.AccelRaw[0]));
                            jLabel_AccelY.setText(Integer.toString(sock.AccelRaw[1]));
                            jLabel_AccelZ.setText(Integer.toString(sock.AccelRaw[2]));
                            jLabel_CompassX.setText(Integer.toString(sock.CompassRaw[0]));
                            jLabel_CompassY.setText(Integer.toString(sock.CompassRaw[1]));
                            jLabel_CompassZ.setText(Integer.toString(sock.CompassRaw[2]));
                            jLabel_IMUSeq.setText(Integer.toString(sock.imuSeq));
                            jLabel_EstYaw.setText(df.format(sock.estYaw));
                            //GPS Sensor
                            jLabel_GPSLat.setText(Double.toString(sock.GPSLat));
                            jLabel_GPSLong.setText(Double.toString(sock.GPSLong));
                            jLabel_GPSTimeStamp.setText(Double.toString(sock.GPSTimeStamp));
                            jLabel_GPSVog.setText(df.format(sock.GPSVog));
                            jLabel_GPSCog.setText(df.format(sock.GPSCog));
                            if (sock.GPSState == 1){
                                jLabel_GPSState.setText("Valid");
                            }
                            else{
                                jLabel_GPSState.setText("InValid");
                            }
                            
                            //send ping command here to keep communicaiton live
                            SendPingCmd();
                         }
                    }   
                 };
        timer  = new Timer(20, actionListener);
        timer.stop();

 
      }
    
    private void SendPingCmd()    {
        sock.sendCommand("PING");
    }
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_RobotIP = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_RobotPort = new javax.swing.JTextField();
        jButton_Connect = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel_BatteryVol = new javax.swing.JLabel();
        jButton_FrontLight = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton_MoveForward = new javax.swing.JButton();
        jButton_TurnLeft = new javax.swing.JButton();
        jButton_Stop = new javax.swing.JButton();
        jButton_TurnRight = new javax.swing.JButton();
        jButton_MoveBackward = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jSlider_Power = new javax.swing.JSlider();
        jLabel_Power = new javax.swing.JLabel();
        jButton_ReleaseEStop = new javax.swing.JButton();
        jButton_EStop = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel_Motor1Pos = new javax.swing.JLabel();
        jLabel_Motor2Pos = new javax.swing.JLabel();
        jLabel_Motor3Pos = new javax.swing.JLabel();
        jLabel_Motor4Pos = new javax.swing.JLabel();
        jLabel_Motor1Speed = new javax.swing.JLabel();
        jLabel_Motor2Speed = new javax.swing.JLabel();
        jLabel_Motor3Speed = new javax.swing.JLabel();
        jLabel_Motor4Speed = new javax.swing.JLabel();
        jLabel_Motor1Power = new javax.swing.JLabel();
        jLabel_Motor2Power = new javax.swing.JLabel();
        jLabel_Motor3Power = new javax.swing.JLabel();
        jLabel_Motor4Power = new javax.swing.JLabel();
        jLabel_Motor1Temp = new javax.swing.JLabel();
        jLabel_Motor2Temp = new javax.swing.JLabel();
        jLabel_Motor3Temp = new javax.swing.JLabel();
        jLabel_Motor4Temp = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel_Motor1Amp = new javax.swing.JLabel();
        jLabel_Motor2Amp = new javax.swing.JLabel();
        jLabel_Motor3Amp = new javax.swing.JLabel();
        jLabel_Motor4Amp = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel_Driver1State = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel_Driver2State = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton_MoveFlipper1Up = new javax.swing.JButton();
        jButton_MoveFlipper1Down = new javax.swing.JButton();
        jButton_MoveFlipper2Up = new javax.swing.JButton();
        jButton_MoveFlipper2Down = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jButton_FliiperArm1Stop = new javax.swing.JButton();
        jButton_FliiperArm2Stop = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel_GyroX = new javax.swing.JLabel();
        jLabel_GyroY = new javax.swing.JLabel();
        jLabel_GyroZ = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel_AccelX = new javax.swing.JLabel();
        jLabel_AccelY = new javax.swing.JLabel();
        jLabel_AccelZ = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel_CompassX = new javax.swing.JLabel();
        jLabel_CompassY = new javax.swing.JLabel();
        jLabel_CompassZ = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel_IMUSeq = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel_EstYaw = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel_GPSLat = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel_GPSLong = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel_GPSTimeStamp = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel_GPSVog = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel_GPSCog = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel_GPSState = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Jaguar Control Demo");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Robot Connection"));
        jPanel1.setName("Robot Connection"); // NOI18N

        jLabel1.setText("Robot IP:");

        jTextField_RobotIP.setText("192.168.0.60");

        jLabel2.setText("Robot Port:");

        jTextField_RobotPort.setText("10001");

        jButton_Connect.setText("Connect");
        jButton_Connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ConnectActionPerformed(evt);
            }
        });

        jLabel10.setText("Battery Vol:");

        jLabel_BatteryVol.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_BatteryVol.setForeground(new java.awt.Color(102, 255, 51));
        jLabel_BatteryVol.setText("0.0");

        jButton_FrontLight.setText("FrontLight ON");
        jButton_FrontLight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_FrontLightMouseClicked(evt);
            }
        });
        jButton_FrontLight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_FrontLightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addComponent(jLabel1)
                .addGap(19, 19, 19)
                .addComponent(jTextField_RobotIP, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_RobotPort, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_Connect, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jLabel_BatteryVol, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_FrontLight)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton_Connect)
                .addComponent(jTextField_RobotPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2)
                .addComponent(jTextField_RobotIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel1)
                .addComponent(jLabel10)
                .addComponent(jLabel_BatteryVol)
                .addComponent(jButton_FrontLight))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Motor Control"));

        jButton_MoveForward.setText("^");
        jButton_MoveForward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_MoveForward.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_MoveForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_MoveForwardActionPerformed(evt);
            }
        });

        jButton_TurnLeft.setText("<");
        jButton_TurnLeft.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_TurnLeft.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_TurnLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_TurnLeftActionPerformed(evt);
            }
        });

        jButton_Stop.setText("O");
        jButton_Stop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_Stop.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_Stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_StopActionPerformed(evt);
            }
        });

        jButton_TurnRight.setText(">");
        jButton_TurnRight.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_TurnRight.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_TurnRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_TurnRightActionPerformed(evt);
            }
        });

        jButton_MoveBackward.setText("v");
        jButton_MoveBackward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_MoveBackward.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_MoveBackward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_MoveBackwardActionPerformed(evt);
            }
        });

        jLabel8.setText("MotorPower:");

        jSlider_Power.setMaximum(1000);
        jSlider_Power.setMinimum(50);
        jSlider_Power.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider_Power.setValue(200);
        jSlider_Power.setMaximumSize(new java.awt.Dimension(50, 1000));
        jSlider_Power.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_PowerStateChanged(evt);
            }
        });
        jSlider_Power.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSlider_PowerPropertyChange(evt);
            }
        });
        jSlider_Power.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jSlider_PowerAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        jLabel_Power.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Power.setLabelFor(jSlider_Power);
        jLabel_Power.setText("200");

        jButton_ReleaseEStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_ReleaseEStop.setLabel("Release EStop");
        jButton_ReleaseEStop.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_ReleaseEStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ReleaseEStopActionPerformed(evt);
            }
        });

        jButton_EStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_EStop.setLabel("EStop");
        jButton_EStop.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_EStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_EStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton_ReleaseEStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton_EStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton_TurnLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jButton_MoveBackward, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)
                                .addComponent(jLabel8))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton_MoveForward, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jButton_Stop, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton_TurnRight, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(37, 37, 37)
                                        .addComponent(jSlider_Power, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(jLabel_Power, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap(58, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton_EStop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_ReleaseEStop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_MoveForward)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Stop)
                    .addComponent(jButton_TurnLeft)
                    .addComponent(jButton_TurnRight))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_MoveBackward)
                .addGap(24, 24, 24))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jSlider_Power, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel_Power)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Motor Sensor"));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("EncoderPos");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("EncoderSpeed");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("PWM");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Temperature");

        jLabel3.setText("Motor #1");

        jLabel24.setText("Motor #2");

        jLabel25.setText("Motor #3");

        jLabel4.setText("Motor #4");

        jLabel_Motor1Pos.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor1Pos.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor1Pos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor1Pos.setText("32767");
        jLabel_Motor1Pos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel_Motor2Pos.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor2Pos.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor2Pos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor2Pos.setText("32767");
        jLabel_Motor2Pos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel_Motor3Pos.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor3Pos.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor3Pos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor3Pos.setText("32767");
        jLabel_Motor3Pos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel_Motor4Pos.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor4Pos.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor4Pos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor4Pos.setText("32767");
        jLabel_Motor4Pos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel_Motor1Speed.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor1Speed.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor1Speed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor1Speed.setText("500");

        jLabel_Motor2Speed.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor2Speed.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor2Speed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor2Speed.setText("500");

        jLabel_Motor3Speed.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor3Speed.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor3Speed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor3Speed.setText("500");

        jLabel_Motor4Speed.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor4Speed.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor4Speed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor4Speed.setText("500");

        jLabel_Motor1Power.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor1Power.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor1Power.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor1Power.setText("0.00");

        jLabel_Motor2Power.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor2Power.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor2Power.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor2Power.setText("0.00");

        jLabel_Motor3Power.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor3Power.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor3Power.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor3Power.setText("0.00");

        jLabel_Motor4Power.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor4Power.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor4Power.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor4Power.setText("0.00");

        jLabel_Motor1Temp.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor1Temp.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor1Temp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor1Temp.setText("0.00");

        jLabel_Motor2Temp.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor2Temp.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor2Temp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor2Temp.setText("0.00");

        jLabel_Motor3Temp.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor3Temp.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor3Temp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor3Temp.setText("0.00");

        jLabel_Motor4Temp.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor4Temp.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor4Temp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor4Temp.setText("0.00");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Current");

        jLabel_Motor1Amp.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor1Amp.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor1Amp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor1Amp.setText("0.00");

        jLabel_Motor2Amp.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor2Amp.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor2Amp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor2Amp.setText("0.00");

        jLabel_Motor3Amp.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor3Amp.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor3Amp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor3Amp.setText("0.00");

        jLabel_Motor4Amp.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Motor4Amp.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Motor4Amp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Motor4Amp.setText("0.00");

        jLabel12.setText("Driver-I State:");

        jLabel_Driver1State.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Driver1State.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Driver1State.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Driver1State.setText("32767");
        jLabel_Driver1State.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel13.setText("Driver-II State:");

        jLabel_Driver2State.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_Driver2State.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_Driver2State.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Driver2State.setText("32767");
        jLabel_Driver2State.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel25)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel_Motor2Pos, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_Motor1Pos, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_Motor3Pos, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_Motor4Pos, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_Motor1Speed, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_Motor2Speed, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_Motor3Speed, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_Motor4Speed, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel_Motor3Power, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                                    .addComponent(jLabel_Motor2Power, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel_Motor1Power, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel_Motor4Power, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel_Motor2Temp, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_Motor1Temp, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_Motor3Temp, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_Motor4Temp, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel_Motor2Amp, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_Motor1Amp, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_Motor3Amp, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_Motor4Amp, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel13)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel_Driver2State, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel12)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel_Driver1State, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel_Motor1Pos)
                            .addComponent(jLabel_Motor1Speed)
                            .addComponent(jLabel_Motor1Power)
                            .addComponent(jLabel_Motor1Temp))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(jLabel_Motor2Pos)
                            .addComponent(jLabel_Motor2Speed)
                            .addComponent(jLabel_Motor2Power)
                            .addComponent(jLabel_Motor2Temp))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(jLabel_Motor3Pos)
                            .addComponent(jLabel_Motor3Speed)
                            .addComponent(jLabel_Motor3Power)
                            .addComponent(jLabel_Motor3Temp))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel_Motor4Pos)
                            .addComponent(jLabel_Motor4Speed)
                            .addComponent(jLabel_Motor4Power)
                            .addComponent(jLabel_Motor4Temp)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel_Motor1Amp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel_Motor2Amp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel_Motor3Amp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel_Motor4Amp)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel_Driver1State))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel_Driver2State))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Jaguar V2 & V4 Flipper Arm Control"));

        jButton_MoveFlipper1Up.setText("^");
        jButton_MoveFlipper1Up.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_MoveFlipper1Up.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_MoveFlipper1Up.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton_MoveFlipper1UpMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_MoveFlipper1UpMouseReleased(evt);
            }
        });
        jButton_MoveFlipper1Up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_MoveFlipper1UpActionPerformed(evt);
            }
        });

        jButton_MoveFlipper1Down.setText("v");
        jButton_MoveFlipper1Down.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_MoveFlipper1Down.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_MoveFlipper1Down.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton_MoveFlipper1DownMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_MoveFlipper1DownMouseReleased(evt);
            }
        });
        jButton_MoveFlipper1Down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_MoveFlipper1DownActionPerformed(evt);
            }
        });

        jButton_MoveFlipper2Up.setText("^");
        jButton_MoveFlipper2Up.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_MoveFlipper2Up.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_MoveFlipper2Up.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton_MoveFlipper2UpMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_MoveFlipper2UpMouseReleased(evt);
            }
        });
        jButton_MoveFlipper2Up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_MoveFlipper2UpActionPerformed(evt);
            }
        });

        jButton_MoveFlipper2Down.setText("v");
        jButton_MoveFlipper2Down.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_MoveFlipper2Down.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_MoveFlipper2Down.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton_MoveFlipper2DownMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_MoveFlipper2DownMouseReleased(evt);
            }
        });
        jButton_MoveFlipper2Down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_MoveFlipper2DownActionPerformed(evt);
            }
        });

        jLabel17.setText("Flipper Arm Channel 1");

        jLabel18.setText("Flipper Arm Channel 2");

        jLabel19.setText("Press the button will move arm, release will stop");

        jLabel20.setText("Jaguar V2 Independ Right or V4 Rear Flipper");

        jButton_FliiperArm1Stop.setText("O");
        jButton_FliiperArm1Stop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_FliiperArm1Stop.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_FliiperArm1Stop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_FliiperArm1StopMouseClicked(evt);
            }
        });
        jButton_FliiperArm1Stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_FliiperArm1StopActionPerformed(evt);
            }
        });

        jButton_FliiperArm2Stop.setText("O");
        jButton_FliiperArm2Stop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_FliiperArm2Stop.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton_FliiperArm2Stop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_FliiperArm2StopMouseClicked(evt);
            }
        });
        jButton_FliiperArm2Stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_FliiperArm2StopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton_MoveFlipper1Down, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton_MoveFlipper2Down, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton_MoveFlipper1Up, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton_FliiperArm1Stop, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton_FliiperArm2Stop, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton_MoveFlipper2Up, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel20))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel17))
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton_MoveFlipper1Up)
                            .addComponent(jButton_MoveFlipper2Up))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton_FliiperArm1Stop)
                            .addComponent(jButton_FliiperArm2Stop))
                        .addGap(12, 12, 12)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_MoveFlipper1Down)
                    .addComponent(jButton_MoveFlipper2Down))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("IMU Sensor"));

        jLabel14.setText("Gyro:");

        jLabel_GyroX.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_GyroX.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_GyroX.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_GyroX.setText("0");
        jLabel_GyroX.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel_GyroY.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_GyroY.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_GyroY.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_GyroY.setText("0");

        jLabel_GyroZ.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_GyroZ.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_GyroZ.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_GyroZ.setText("0");

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("X Axis");

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Y Axis");

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Z Axis");

        jLabel22.setText("Accel:");

        jLabel_AccelX.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_AccelX.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_AccelX.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_AccelX.setText("0");
        jLabel_AccelX.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel_AccelY.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_AccelY.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_AccelY.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_AccelY.setText("0");

        jLabel_AccelZ.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_AccelZ.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_AccelZ.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_AccelZ.setText("0");

        jLabel23.setText("Compass:");

        jLabel_CompassX.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_CompassX.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_CompassX.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_CompassX.setText("0");
        jLabel_CompassX.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel_CompassY.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_CompassY.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_CompassY.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_CompassY.setText("0");

        jLabel_CompassZ.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_CompassZ.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_CompassZ.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_CompassZ.setText("0");

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Seq:");

        jLabel_IMUSeq.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_IMUSeq.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_IMUSeq.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_IMUSeq.setText("0");

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Est Yaw:");

        jLabel_EstYaw.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_EstYaw.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_EstYaw.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_EstYaw.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(202, 202, 202))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(19, 19, 19)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel22)
                                            .addComponent(jLabel14))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel_GyroX, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_AccelX, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_CompassX, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel_CompassY, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_AccelY, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_GyroY, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel_AccelZ, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                                    .addComponent(jLabel_CompassZ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel_GyroZ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGap(217, 217, 217)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(53, 53, 53)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_IMUSeq, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                    .addComponent(jLabel_EstYaw, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jLabel_GyroX)
                            .addComponent(jLabel_GyroY)
                            .addComponent(jLabel_GyroZ)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel_IMUSeq)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(jLabel_AccelX)
                            .addComponent(jLabel_AccelY)
                            .addComponent(jLabel_AccelZ))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(jLabel_CompassX)
                            .addComponent(jLabel_CompassY)
                            .addComponent(jLabel_CompassZ)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel_EstYaw)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("GPS Sensor"));

        jLabel28.setText("Latitude:");

        jLabel_GPSLat.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_GPSLat.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_GPSLat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_GPSLat.setText("0");
        jLabel_GPSLat.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel29.setText("Longitude:");

        jLabel_GPSLong.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_GPSLong.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_GPSLong.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_GPSLong.setText("0");
        jLabel_GPSLong.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel30.setText("TimeStamp:");

        jLabel_GPSTimeStamp.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_GPSTimeStamp.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_GPSTimeStamp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_GPSTimeStamp.setText("0");
        jLabel_GPSTimeStamp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel31.setText("VOG:");

        jLabel_GPSVog.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_GPSVog.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_GPSVog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_GPSVog.setText("0");
        jLabel_GPSVog.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel32.setText("COG::");

        jLabel_GPSCog.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_GPSCog.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_GPSCog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_GPSCog.setText("0");
        jLabel_GPSCog.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel33.setText("State:");

        jLabel_GPSState.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel_GPSState.setForeground(new java.awt.Color(0, 255, 0));
        jLabel_GPSState.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_GPSState.setText("0");
        jLabel_GPSState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel33)
                            .addComponent(jLabel30))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_GPSTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                            .addComponent(jLabel_GPSState, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel28)
                            .addComponent(jLabel29))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_GPSLat, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                            .addComponent(jLabel_GPSLong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel_GPSCog, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel_GPSVog, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel_GPSLat)
                    .addComponent(jLabel31)
                    .addComponent(jLabel_GPSVog))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jLabel_GPSLong)
                    .addComponent(jLabel32)
                    .addComponent(jLabel_GPSCog))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel_GPSTimeStamp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jLabel_GPSState))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(32, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(31, 31, 31)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(117, 117, 117))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    
    private void jButton_ConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ConnectActionPerformed
// TODO add your handling code here:
            robotIP = jTextField_RobotIP.getText();
            robotPort = Integer.parseInt(jTextField_RobotPort.getText());
            sock = new robotSocket(robotIP, robotPort);
            ConnectFlag = true;
            
            Thread worker = new Thread(sock);
            worker.setDaemon(true);
            worker.start();
 
            
            timer.start();
            
            System.out.println(sock);
            u.SetSocket(sock);
            
    }//GEN-LAST:event_jButton_ConnectActionPerformed

    private void jSlider_PowerAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jSlider_PowerAncestorAdded

    }//GEN-LAST:event_jSlider_PowerAncestorAdded

    private void jSlider_PowerPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSlider_PowerPropertyChange

    }//GEN-LAST:event_jSlider_PowerPropertyChange

    private void jSlider_PowerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_PowerStateChanged
        // TODO add your handling code here:
        jLabel_Power.setText(Integer.toString(jSlider_Power.getValue()));
    }//GEN-LAST:event_jSlider_PowerStateChanged

    private void jButton_MoveBackwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_MoveBackwardActionPerformed
        // TODO add your handling code here:
        int power = jSlider_Power.getValue();
        String strCmd = "MMW !M " + Integer.toString(-power) + " " + Integer.toString(power);
        sock.sendCommand(strCmd);

    }//GEN-LAST:event_jButton_MoveBackwardActionPerformed

    private void jButton_TurnRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_TurnRightActionPerformed
        // TODO add your handling code here:
        int power = jSlider_Power.getValue();
        String strCmd = "MMW !M " + Integer.toString(power) + " " + Integer.toString(power);
        sock.sendCommand(strCmd);

    }//GEN-LAST:event_jButton_TurnRightActionPerformed

    private void jButton_StopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_StopActionPerformed
        // TODO add your handling code here:
        sock.sendCommand("MMW !M 0 0");
    }//GEN-LAST:event_jButton_StopActionPerformed

    private void jButton_TurnLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_TurnLeftActionPerformed
        // TODO add your handling code here:
        int power = jSlider_Power.getValue();
        String strCmd = "MMW !M " + Integer.toString(-power) + " " + Integer.toString(-power);
        sock.sendCommand(strCmd);
    }//GEN-LAST:event_jButton_TurnLeftActionPerformed

    private void jButton_MoveForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_MoveForwardActionPerformed
        // TODO add your handling code here:
        int power = jSlider_Power.getValue();
        String strCmd = "MMW !M " + Integer.toString(power) + " " + Integer.toString(-power);
        sock.sendCommand(strCmd);
    }//GEN-LAST:event_jButton_MoveForwardActionPerformed

    private void jButton_FrontLightMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_FrontLightMouseClicked
        // TODO add your handling code here:
        int powerIO = 0x7f;
        String strCmd;
        if (jButton_FrontLight.getText().equals("FrontLight ON"))
        {
            jButton_FrontLight.setText("FrontLight OFF");
            powerIO = powerIO | 0x80;
            strCmd = "SYS MMC " + Integer.toString(powerIO);
            sock.sendCommand(strCmd);
        }
        else
        {
            jButton_FrontLight.setText("FrontLight ON");
            powerIO = powerIO & 0x7f;
            strCmd = "SYS MMC " + Integer.toString(powerIO);
            sock.sendCommand(strCmd);

        }
    }//GEN-LAST:event_jButton_FrontLightMouseClicked

    private void jButton_MoveFlipper1UpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper1UpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton_MoveFlipper1UpActionPerformed

    private void jButton_MoveFlipper1DownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper1DownActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton_MoveFlipper1DownActionPerformed

    private void jButton_MoveFlipper2UpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper2UpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton_MoveFlipper2UpActionPerformed

    private void jButton_MoveFlipper2DownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper2DownActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton_MoveFlipper2DownActionPerformed

    private void jButton_MoveFlipper1UpMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper1UpMousePressed
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton_MoveFlipper1UpMousePressed

    private void jButton_MoveFlipper1UpMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper1UpMouseReleased
        // TODO add your handling code here:
        stopFlipperArm1();
    }//GEN-LAST:event_jButton_MoveFlipper1UpMouseReleased

    private void jButton_MoveFlipper1DownMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper1DownMousePressed
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton_MoveFlipper1DownMousePressed

    private void jButton_MoveFlipper1DownMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper1DownMouseReleased
        // TODO add your handling code here:
        stopFlipperArm1();
    }//GEN-LAST:event_jButton_MoveFlipper1DownMouseReleased

    private void jButton_MoveFlipper2UpMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper2UpMousePressed
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton_MoveFlipper2UpMousePressed

    private void jButton_MoveFlipper2UpMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper2UpMouseReleased
        // TODO add your handling code here:
        stopFlipperArm2();
    }//GEN-LAST:event_jButton_MoveFlipper2UpMouseReleased

    private void jButton_MoveFlipper2DownMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper2DownMousePressed
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton_MoveFlipper2DownMousePressed

    private void jButton_MoveFlipper2DownMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_MoveFlipper2DownMouseReleased
        // TODO add your handling code here:
        stopFlipperArm2();
    }//GEN-LAST:event_jButton_MoveFlipper2DownMouseReleased

    private void jButton_FliiperArm1StopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_FliiperArm1StopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton_FliiperArm1StopActionPerformed

    private void jButton_FliiperArm2StopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_FliiperArm2StopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton_FliiperArm2StopActionPerformed

    private void jButton_FliiperArm1StopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_FliiperArm1StopMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton_FliiperArm1StopMouseClicked

    private void jButton_FliiperArm2StopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_FliiperArm2StopMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton_FliiperArm2StopMouseClicked

    private void jButton_ReleaseEStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ReleaseEStopActionPerformed
        // TODO add your handling code here:
        sock.sendCommand("MMW !MG");
    }//GEN-LAST:event_jButton_ReleaseEStopActionPerformed

    private void jButton_EStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_EStopActionPerformed
        // TODO add your handling code here:
        sock.sendCommand("MMW !EX");
    }//GEN-LAST:event_jButton_EStopActionPerformed

    private void jButton_FrontLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_FrontLightActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jButton_FrontLightActionPerformed
    
    private void stopFlipperArm1(){

    }
    
    private void stopFlipperArm2(){

    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    	
    	//robotIP = "192.168.1.60";
        //robotPort = Integer.parseInt(10001);
        //sock = new robotSocket("192.168.1.60", 10001);
        //ConnectFlag = true;
        
        //Thread worker = new Thread(sock);
        //worker.setDaemon(true);
        //worker.start();

        
        //timer.start();
        
        //String strCmd = "MMW !M " + Integer.toString(100) + " " + Integer.toString(100);
        //sock.sendCommand(strCmd);
    	System.out.println("HELLO");
    	
    	
    	
    	
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JaguarControlDemo().setVisible(true);
                 
        };
        
        
        
        });
        
        u = new UnitySub("roverTopic", "tcp://localhost:1883", "roverSubscriber", sock);
        
        
     }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Connect;
    private javax.swing.JButton jButton_EStop;
    private javax.swing.JButton jButton_FliiperArm1Stop;
    private javax.swing.JButton jButton_FliiperArm2Stop;
    private javax.swing.JButton jButton_FrontLight;
    private javax.swing.JButton jButton_MoveBackward;
    private javax.swing.JButton jButton_MoveFlipper1Down;
    private javax.swing.JButton jButton_MoveFlipper1Up;
    private javax.swing.JButton jButton_MoveFlipper2Down;
    private javax.swing.JButton jButton_MoveFlipper2Up;
    private javax.swing.JButton jButton_MoveForward;
    private javax.swing.JButton jButton_ReleaseEStop;
    private javax.swing.JButton jButton_Stop;
    private javax.swing.JButton jButton_TurnLeft;
    private javax.swing.JButton jButton_TurnRight;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_AccelX;
    private javax.swing.JLabel jLabel_AccelY;
    private javax.swing.JLabel jLabel_AccelZ;
    private javax.swing.JLabel jLabel_BatteryVol;
    private javax.swing.JLabel jLabel_CompassX;
    private javax.swing.JLabel jLabel_CompassY;
    private javax.swing.JLabel jLabel_CompassZ;
    private javax.swing.JLabel jLabel_Driver1State;
    private javax.swing.JLabel jLabel_Driver2State;
    private javax.swing.JLabel jLabel_EstYaw;
    private javax.swing.JLabel jLabel_GPSCog;
    private javax.swing.JLabel jLabel_GPSLat;
    private javax.swing.JLabel jLabel_GPSLong;
    private javax.swing.JLabel jLabel_GPSState;
    private javax.swing.JLabel jLabel_GPSTimeStamp;
    private javax.swing.JLabel jLabel_GPSVog;
    private javax.swing.JLabel jLabel_GyroX;
    private javax.swing.JLabel jLabel_GyroY;
    private javax.swing.JLabel jLabel_GyroZ;
    private javax.swing.JLabel jLabel_IMUSeq;
    private javax.swing.JLabel jLabel_Motor1Amp;
    private javax.swing.JLabel jLabel_Motor1Pos;
    private javax.swing.JLabel jLabel_Motor1Power;
    private javax.swing.JLabel jLabel_Motor1Speed;
    private javax.swing.JLabel jLabel_Motor1Temp;
    private javax.swing.JLabel jLabel_Motor2Amp;
    private javax.swing.JLabel jLabel_Motor2Pos;
    private javax.swing.JLabel jLabel_Motor2Power;
    private javax.swing.JLabel jLabel_Motor2Speed;
    private javax.swing.JLabel jLabel_Motor2Temp;
    private javax.swing.JLabel jLabel_Motor3Amp;
    private javax.swing.JLabel jLabel_Motor3Pos;
    private javax.swing.JLabel jLabel_Motor3Power;
    private javax.swing.JLabel jLabel_Motor3Speed;
    private javax.swing.JLabel jLabel_Motor3Temp;
    private javax.swing.JLabel jLabel_Motor4Amp;
    private javax.swing.JLabel jLabel_Motor4Pos;
    private javax.swing.JLabel jLabel_Motor4Power;
    private javax.swing.JLabel jLabel_Motor4Speed;
    private javax.swing.JLabel jLabel_Motor4Temp;
    private javax.swing.JLabel jLabel_Power;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSlider jSlider_Power;
    private javax.swing.JTextField jTextField_RobotIP;
    private javax.swing.JTextField jTextField_RobotPort;
    // End of variables declaration//GEN-END:variables
    
}
