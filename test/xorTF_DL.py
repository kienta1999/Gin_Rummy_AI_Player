import tensorflow as tf
import numpy as np
import pandas as pd

data = pd.read_csv('XORtrain.csv')
inputs =   np.array(data[['num1', 'num2']])
targets = np.array(data['xor'])


input_size = inputs.shape[1] #2
output_size = 1
hidden_layer_size = 3


model = tf.keras.Sequential([
    tf.keras.layers.Dense(hidden_layer_size, activation = 'relu'), #1st hidden layer dot product of input and weight + bias
    tf.keras.layers.Dense(hidden_layer_size, activation = 'relu'),
    tf.keras.layers.Dense(output_size, activation = 'softmax')
])


model.compile(optimizer='adam', loss='mean_squared_error', metrics = ['accuracy'])


max_epochs = 30
batch_size = 100

#early_stopping = tf.keras.callbacks.EarlyStopping(patience=2) 
#stop training when the validation loss starts increasing
#patience: how many consecutive increase we can tolerate

model.fit(inputs,
         targets,
         batch_size = batch_size,
         epochs = max_epochs,
          callbacks = [early_stopping],
          verbose=2)

test_loss, test_accuracy = model.evaluate(inputs, targets)

print('test_loss ', test_loss)

print('test_accuracy ', test_accuracy)

input_test = [[0,0]]
print('Prediction ', model.predict(input_test))