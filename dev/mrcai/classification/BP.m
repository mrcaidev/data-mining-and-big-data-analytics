clear all;
close all;
clc;
warning off;

p = [0 1 2 3 4 5 6 7 8];
t = [0 0.84 0.91 0.14 -0.77 -0.96 -0.28 0.66 0.99];

network = newff([0 8], [10 1],{'tansig' 'purelin'},'trainlm');
y1 = sim(network,p);
plot(p, t, 'o', p, y1, 'x');

network.trainParam.epochs = 50;
network.trainParam.goal = 0.01;
network = train(network, p, t);
y2 = sim(network,p);

test = 6.5;
y3 = sim(network,test);
plot(p, t, 'o', p, y1, 'x', p, y2, '*');
