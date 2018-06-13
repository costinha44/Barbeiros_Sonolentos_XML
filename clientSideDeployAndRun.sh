echo "Compressing data to be sent to the client side node."
rm -rf clientSide.zip
zip -rq clientSide.zip clientSide comInf
echo "Transfering data to the client side node."
sshpass -f password ssh ruib@l040101-ws01.ua.pt 'mkdir -p teste/BarbeirosSonolentosSocketCSXML'
sshpass -f password ssh ruib@l040101-ws01.ua.pt 'rm -rf teste/BarbeirosSonolentosSocketCSXML/*'
sshpass -f password scp clientSide.zip ruib@l040101-ws01.ua.pt:teste/BarbeirosSonolentosSocketCSXML
echo "Decompressing data sent to the client side node."
sshpass -f password ssh ruib@l040101-ws01.ua.pt 'cd teste/BarbeirosSonolentosSocketCSXML ; unzip -uq clientSide.zip'
echo "Compiling program files at the client side node."
sshpass -f password ssh ruib@l040101-ws01.ua.pt 'cd teste/BarbeirosSonolentosSocketCSXML ; javac */*.java'
sleep 3
echo "Executing program at the client side node."
sshpass -f password ssh ruib@l040101-ws01.ua.pt 'cd teste/BarbeirosSonolentosSocketCSXML ; java clientSide.ClientSleepingBarbers'
