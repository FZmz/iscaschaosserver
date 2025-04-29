mv ./chaosd-latest-linux-amd64 /usr/local/chaosd
export PATH=$PATH:/usr/local/chaosd
echo "export PATH=$PATH:/usr/local/chaosd" >> /etc/profile

source /etc/profile

nohup chaosd server > /data/mj/chaosd/chaosd.log &