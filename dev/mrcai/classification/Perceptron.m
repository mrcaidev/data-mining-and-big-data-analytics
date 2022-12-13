close all;
clc;

P = [-0.5 -0.5 0.3 0; -0.5 0.5 -0.5 1];
T = [1 1 0 0];
plotpv(P, T);

network = newp([-1 1; 0 1], 1);
network = init(network);
y = sim(network, P);
e = T - y;
w = network.IW{1, 1};
b = network.b{1};
plotpc(w, b);

while (mae(e) > 0.0015)
    dw = learnp(w, P, [], [], [], [], e, [], [], [], [], []);
    db = learnp(b, ones(1, 4), [], [], [], [], e, [], [], [], [], []);
    w = w + dw;
    b = b + db;
    network.IW{1, 1} = w;
    network.b{1} = b;
    plotpc(w, b);
    pause;
    y = sim(network, P);
    e = T - y;
end
