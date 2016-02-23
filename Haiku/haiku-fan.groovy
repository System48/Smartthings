/*
 * Haiku Fan Control
*/

preferences {
	input("devname", "text", title: "Device Name", description: "The devices name",required:true)
	input("destIp", "text", title: "IP", description: "The device IP",required:true)
 }

metadata {
	definition (name: "Haiku Fan Control", namespace: "System48/Smartthings/Haiku", author: "System48") {
	capability "Switch"
	capability "Switch Level"
        command "fanon"
        command "fanoff"
        command "setfanlevel"
        command "lighton"
        command "lightoff"
        command "setlightlevel"
        
      	}

	simulator {
		// TODO-: define status and reply messages here
	}

	tiles {
    	standardTile("fan", "device.fan", width: 1, height: 1, canChangeIcon: false, canChangeBackground: true) {
            state "fanon", label: 'On', action:"fanoff", backgroundColor: "#79b821", icon:"st.Lighting.light24"
            state "fanoff", label: 'Off', action:"fanon", backgroundColor: "#ffffff", icon:"st.Lighting.light24"
        }
        controlTile("fanlevel", "device.fanlevel", "slider", height: 1, width: 2, inactiveLabel: false, range: "(0..7)") {
			state "fanlevel", label: '${name}', action:"setfanlevel"
		}
		standardTile("light", "device.light", width: 1, height: 1, canChangeIcon: false, canChangeBackground: true) {
            state "lighton", label: 'On', action:"lightoff", backgroundColor: "#79b821", icon:"st.Lighting.light13"
            state "lightoff", label: 'Off', action:"lighton", backgroundColor: "#ffffff", icon:"st.Lighting.light13"
        }
        controlTile("lightlevel", "device.lightlevel", "slider", height: 1, width: 2, inactiveLabel: false, range: "(0..16)") {
			state "lightlevel", label: '${name}', action:"setlightlevel"
		}
   
		main "fan"
        details(["fan","fanlevel","light","lightlevel"])
	}
}


def parse(String description) {
	log.debug "Parsing '${description}'"
}


def fanon() {
	sendEvent(name: "fan", value: 'fanon')
	request("<" + devname+ ";FAN;PWR;ON>")
}

def fanoff() { 
	sendEvent(name: "fan", value: 'fanoff')
	request("<" + devname + ";FAN;PWR;OFF>")
}

def setfanlevel(val) {
	if (val==0) {
		sendEvent(name: "fan", value: "fanoff")
    } else {
    	sendEvent(name: "fan", value: "fanon")
    }
    sendEvent(name: "fanlevel", value: val)    
    request("<" + devname + ";FAN;SPD;SET;" + val + ">")
}

def lighton() {
	sendEvent(name: "light", value: 'lighton')
	request("<" + devname + ";LIGHT;PWR;ON>")
}

def lightoff() { 
	sendEvent(name: "light", value: 'lightoff')
	request("<" + devname + ";LIGHT;PWR;OFF>")
}

def setlightlevel(val) {
	if (val==0) {
    	sendEvent(name: "light", value: "lightoff")
    } else {    
		sendEvent(name: "light", value: "lighton")
    }
    sendEvent(name: "lightlevel", value: val)    
    request("<" + devname + ";LIGHT;LEVEL;SET;" + val + ">")
}

def request(body) { 

    def hosthex = convertIPtoHex(destIp)
    def porthex = convertPortToHex(31415)
    device.deviceNetworkId = "$hosthex:$porthex" 	
    def cmds = []
    def hubAction = new physicalgraph.device.HubAction(body,physicalgraph.device.Protocol.LAN)
	cmds << hubAction

    log.debug cmds
        
    cmds
}


private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04X', port.toInteger() )
    return hexport
}
