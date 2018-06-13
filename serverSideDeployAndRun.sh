echo "Compressing data to be sent to the server side node."
rm -rf serverSide.zip
zip -rq serverSide.zip serverSide comInf
echo "Transfering data to the server side node."
sshpass -f password ssh ruib@l040101-ws04.ua.pt 'mkdir -p teste/BarbeirosSonolentosSocketCSXML'
sshpass -f password ssh ruib@l040101-ws04.ua.pt 'rm -rf teste/BarbeirosSonolentosSocketCSXML/*'
sshpass -f password scp serverSide.zip ruib@l040101-ws04.ua.pt:teste/BarbeirosSonolentosSocketCSXML
echo "Decompressing data sent to the server side node."
sshpass -f password ssh ruib@l040101-ws04.ua.pt 'cd teste/BarbeirosSonolentosSocketCSXML ; unzip -uq serverSide.zip'
echo "Compiling program files at the server side node."
sshpass -f password ssh ruib@l040101-ws04.ua.pt 'cd teste/BarbeirosSonolentosSocketCSXML ; javac */*.java'
echo "End of compiling at the server side node."
sleep 1
echo "Executing program at the server side node."
sshpass -f password ssh ruib@l040101-ws04.ua.pt 'cd teste/BarbeirosSonolentosSocketCSXML ; java serverSide.ServerSleepingBarbers'
echo "Server  shutdown."
sshpass -f password ssh ruib@l040101-ws04.ua.pt 'cd teste/BarbeirosSonolentosSocketCSXML ; less stat'
