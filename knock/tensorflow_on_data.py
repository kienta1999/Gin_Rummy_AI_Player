import tensorflow.compat.v1 as tf
tf.compat.v1.disable_eager_execution()
import numpy as np
import pandas as pd

data = pd.read_csv('data.csv')

iterations = 10000
learn_rate = 0.005

#print(data.columns)

list_input = np.array(data.drop('finalScore', axis = 1))
list_output = np.array(data['finalScore']).reshape(-1, 1)


tensor_input = tf.convert_to_tensor(list_input, dtype=tf.float64)
tensor_output = tf.convert_to_tensor(list_output, dtype=tf.float64)

print(tensor_input.shape)

x = tf.placeholder(tf.float32, [None, 15], name="x")
W = tf.Variable(tf.zeros([15, 1]), name="W")
b = tf.Variable(tf.zeros([1]), name="b")
y = tf.placeholder(tf.float32, [None, 1], name = "y")

model = tf.add(tf.matmul(x, W), b)

cost = tf.reduce_mean(tf.square(y - model))
train = tf.train.GradientDescentOptimizer(learn_rate).minimize(cost)

init = tf.global_variables_initializer()
builder = tf.saved_model.builder.SavedModelBuilder('./model')


with tf.Session() as session:
    session.run(init)

    for _ in range(iterations):

        session.run(train, feed_dict={
            x: list_input,
            y: list_output
        })
        builder.add_meta_graph_and_variables(session, [tf.saved_model.tag_constants.SERVING])
        builder.save()
    #print("cost = {}".format(session.run(cost, feed_dict={
    #    x: tensor_input_xor,
    #    y: tensor_output_xor
    #})))

    print("W = {}".format(session.run(W)))
    print("b = {}".format(session.run(b)))
    
    print("end")