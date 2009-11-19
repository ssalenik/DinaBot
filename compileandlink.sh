#/bin/bash

if [ $# -eq 1 -o $# -eq 2 ]
	then
	
	if [ $1 = "c" ]
		then
		echo "Cleaning ..."
		rm -f log.txt DinaBOTSlave.nxj DinaBOTMaster.nxj
		exit 0
		fi
	fi
	
	echo "Setup ..."
	
	if [ -f log.txt ]
		then
		rm log.txt
	fi
		
	if ! mkdir bin
		then
		exit 1
	fi
	if ! cd bin
		then
		exit 1
	fi
	
	echo "Build and Link ..."
	
	if [ $1 = "m" ]
		then
		if ! nxjc ../dinaBOT/DinaBOTMaster.java -d ./ -sourcepath ../
			then
			cd ..
			rm -rf bin
			exit 1
		fi

		if ! nxjlink dinaBOT/DinaBOTMaster -cp ./ -o ../DinaBOTMaster.nxj -v > ../log.txt
			then
			cd ..
			rm -rf bin
			exit 1
		fi
		
		cd ..
		
		if [ $# -eq 2 ]
			then
			if [ $2 = "f" ]
				then
				echo "Flashing ..."
				nxjupload DinaBOTMaster.nxj -u
			fi
		fi
	fi
	
	if [ $1 = "s" ]
		then
		if ! nxjc ../dinaBOT/DinaBOTSlave.java -d ./ -sourcepath ../
			then
			cd ..
			rm -rf bin
			exit 1
		fi
		
		if ! nxjlink dinaBOT/DinaBOTSlave -cp ./ -o ../DinaBOTSlave.nxj -v > ../log.txt
			then
			cd ..
			rm -rf bin
			exit 1
		fi
		
		cd ..
		
		if [ $# -eq 2 ]
			then
			if [ $2 = "f" ]
				then
				echo "Flashing ..."
				nxjupload DinaBOTSlave.nxj -u
			fi
		fi
	fi	
	
	rm -rf bin
	exit 0
fi
exit 1