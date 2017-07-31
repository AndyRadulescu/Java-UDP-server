This is a Java FX game , a table Tennis . To move a racket LMB must be pressed and the mouse pointer dragged up or down. The mouse pointer has to be inside the scene(window) to be able to get its position. For a good practice I used "KeyFrames" in JavaFX as a thread.The thread sleeps every 0.005 seconds.

The position of the ball and rackets is saved in some global fields.When the ball touches the rcaket, depending how high or low on the racket ,it changes its angle so if the ball hited the racket exactly in the top , the ball's angle will be near vertical so the ball will go almost streight up.

The server is a UDP configuration , which sends and recives messages. The messages are serialized ,encrypted and decrypted , when sent to and received from the server. When the application starts it sends a message to the server to see wich client is now to be connected. Maximum 2 clients. Every 0.005 seconds a message is sent to the server and received. The ball positions along with the rackets position are sent to the server. The server sends back the message to both clients to syncronize them.

The message is serialized , that means , to the server is sent just a string . First in the message is the action that needs to be done , an int value. After each value that is serialized ";" is inserted to know when the value is ended. This is useful when deserializating. example of message : action + ";" + "Received!" + ";" . The encryption of the message is done by a XOR function. On both sides ,server and clinet ,there is a key that is used to encrypt or derypt the message.

The application is not finished due to a malfunction in the server , this does not allow the clients to syncronize correctly.
