application_name=NodResponse
ip_address=192.168.100.109

ssh root@${ip_address} mkdir -p /home/vstone/vstonemagic/app/jar/${application_name}/
scp ./${application_name}.jar root@${ip_address}:/home/vstone/vstonemagic/app/jar/${application_name}/
ssh root@${ip_address}

