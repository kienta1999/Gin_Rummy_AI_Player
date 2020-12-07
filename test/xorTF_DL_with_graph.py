import tensorflow.compat.v1 as tf
#tf.disable_v2_behavior()
import numpy as np

test_data_size = 2000
iterations = 10000
learn_rate = 0.005

list_input_xor = np.array([[0,0],[0,1],[1,0],[1,1]])
list_output_xor = np.array([[0],[1],[1],[0]])


tensor_input_xor = tf.convert_to_tensor(list_input_xor, dtype=tf.float64)
tensor_output_xor = tf.convert_to_tensor(list_output_xor, dtype=tf.float64)

print(tensor_input_xor)


x = tf.placeholder(tf.float32, [None, 2], name="x")
W = tf.Variable(tf.zeros([2, 1]), name="W")
b = tf.Variable(tf.zeros([1]), name="b")
y = tf.placeholder(tf.float32, [None, 1], name = "y")

model = tf.add(tf.matmul(x, W), b)

cost = tf.reduce_mean(tf.square(y - model))
train = tf.train.GradientDescentOptimizer(learn_rate).minimize(cost)

init = tf.global_variables_initializer()

# Add ops to save and restore all the variables.
#saver = tf.train.Saver()
builder = tf.saved_model.builder.SavedModelBuilder('./model')

with tf.Session() as session:
    session.run(init)

    for _ in range(iterations):

        session.run(train, feed_dict={
            x: list_input_xor,
            y: list_output_xor
        })
        builder.add_meta_graph_and_variables(session, [tf.saved_model.tag_constants.SERVING])
        builder.save()
    #print("cost = {}".format(session.run(cost, feed_dict={
    #    x: tensor_input_xor,
    #    y: tensor_output_xor
    #})))

    print("W = {}".format(session.run(W)))
    print("b = {}".format(session.run(b)))
    
   # tf.saved_model.save(model, "model")