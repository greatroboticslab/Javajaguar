# Javajaguar

In this code, I am attempting to get a previously written Java GUI program to send ROS commands to a Jaguar 4x4 rover by taking in input from a MQTT publisher.

# Mosquitto

Mosquitto (or another MQTT broker program) is required to interface with. The Java program will attempt to connect to a localhost broker, and read input in the form of int int int as input, or RELEASE_ESTOP to release the emergency stop.
