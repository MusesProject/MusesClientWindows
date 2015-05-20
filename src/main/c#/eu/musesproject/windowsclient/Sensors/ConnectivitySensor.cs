/*
 * #%L
 * museswindowsapp
 * %%
 * Copyright (C) 2013 - 2015 HITEC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Text.RegularExpressions;
using NativeWifi;

namespace Sensors
{
    public class ConnectivitySensor : ISensor
    {
        public string Type { get; set; }
        public bool deviceconnected { get; set; }
        public bool wifienabled { get; set; }
        public bool wificonnected { get; set; }
        public string wifiencryption { get; set; }//Important
        //public int wifineighbors { get; set; }
        //public bool hiddenssid { get; set; }
        public string bssid { get; set; }
        public string networkid { get; set; }
        public string networkname { get; set; }
        //BluetoothConnected should be enum MyEnum{FALSE, TRUE, NOR_SUPPORTED} 
        public bool ethernetconnected { get; set; }
        public bool airplanemode { get; set; }
        public bool connectedtotrustediprange { get; set; }//VPN is connected or not? Important
        public string ipaddress { get; set; }
        public void Update()
        {
            deviceconnected = CheckInternetConnection();
            bssid = String.Join(",", GetBSSID());
            networkid = GetNetworkId();
            networkname = GetNetworkName();
            ipaddress = GetIpAddress();
            ethernetconnected = IsEthernetConnected();
            wificonnected = IsWifiConnected();
            wifienabled = wificonnected;
            connectedtotrustediprange = CheckVPNConnection();
            wifiencryption = GetEncryption();
        }

        private NetworkInterface GetConnectedInterface()
        {
            var interfaces = NetworkInterface.GetAllNetworkInterfaces();
            return interfaces.FirstOrDefault(Interface => Interface.OperationalStatus == OperationalStatus.Up);
        }

        private bool CheckVPNConnection()
        {
            if (!NetworkInterface.GetIsNetworkAvailable()) return false;
            var interfaces = NetworkInterface.GetAllNetworkInterfaces();
            return interfaces.Where(Interface => Interface.OperationalStatus == OperationalStatus.Up).Any(
                Interface => (Interface.NetworkInterfaceType == NetworkInterfaceType.Ppp) &&
                             (Interface.NetworkInterfaceType != NetworkInterfaceType.Loopback));
        }

        private string GetEncryption()
        {
            var encryption = "";
            var wlanClient = new WlanClient();
            var tmp = wlanClient.Interfaces.FirstOrDefault();
            if (tmp == null || tmp.InterfaceState != Wlan.WlanInterfaceState.Connected) return encryption;
            var profile = tmp.GetProfileXml(tmp.CurrentConnection.profileName);
            //var xml = XDocument.Parse(profile);
            try
            {
                var regex = new Regex("<encryption>(.*)</encryption>");
                encryption = regex.Match(profile).Groups[1].ToString();
            }
            catch (Exception) { }
            return encryption;
        }

        private bool CheckInternetConnection()
        {
            try
            {
                var pingSender = new Ping();
                var reply = pingSender.Send("www.google.com");
                return reply != null && reply.Status == IPStatus.Success;
            }
            catch
            {
                return false;
            }

        }

        private List<string> GetBSSID()
        {
            var bssid = new List<string>();
            var wlanClient = new WlanClient();
            foreach (WlanClient.WlanInterface wlanInterface in wlanClient.Interfaces)
            {
                Wlan.WlanBssEntry[] wlanBssEntries = wlanInterface.GetNetworkBssList();
                foreach (Wlan.WlanBssEntry wlanBssEntry in wlanBssEntries)
                {
                    byte[] macAddr = wlanBssEntry.dot11Bssid;
                    var macAddrLen = (uint)macAddr.Length;
                    var str = new string[(int)macAddrLen];
                    for (int i = 0; i < macAddrLen; i++)
                    {
                        str[i] = macAddr[i].ToString("x2");
                    }
                    string mac = string.Join("", str);
                    bssid.Add(mac);
                }
            }

            return bssid;
        }

        private string GetNetworkId()
        {
            var id = "";
            var wlanClient = new WlanClient();
            if (!wlanClient.Interfaces.Any()) return id;
            var tmp = wlanClient.Interfaces.FirstOrDefault();
            if (tmp != null && tmp.InterfaceState == Wlan.WlanInterfaceState.Connected)
            {
                id = tmp.NetworkInterface.Id;
                
            }
            else
            {
                var connectedInterface = GetConnectedInterface();
                id = connectedInterface != null ? connectedInterface.Id : "";
            }
            return id;
        }
        private string GetNetworkName()
        {
            var name = "";
            var wlanClient = new WlanClient();
            if (!wlanClient.Interfaces.Any()) return name;
            var tmp = wlanClient.Interfaces.FirstOrDefault();
            if (tmp != null && tmp.InterfaceState == Wlan.WlanInterfaceState.Connected)
            {
                name = tmp.CurrentConnection.profileName; 
                
            }
            else
            {
                var connectedInterface = GetConnectedInterface();
                name = connectedInterface != null ? connectedInterface.Name : "";
            }

            return name;
        }

        private string GetIpAddress()
        {
            var localIP = "?";
            var host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (var ip in host.AddressList.Where(ip => ip.AddressFamily == AddressFamily.InterNetwork))
            {
                localIP = ip.ToString();
            }
            return localIP;
        }

        private bool IsEthernetConnected()
        {
            var wlanClient = new WlanClient();
            if (!wlanClient.Interfaces.Any()) return false;
            var tmp = GetConnectedInterface();
            return tmp != null && (tmp.NetworkInterfaceType == NetworkInterfaceType.Ethernet ||
                                   tmp.NetworkInterfaceType == NetworkInterfaceType.Ethernet3Megabit ||
                                   tmp.NetworkInterfaceType == NetworkInterfaceType.FastEthernetFx ||
                                   tmp.NetworkInterfaceType == NetworkInterfaceType.FastEthernetT ||
                                   tmp.NetworkInterfaceType == NetworkInterfaceType.GigabitEthernet);
        }

        private bool IsWifiConnected()
        { 
            var wlanClient = new WlanClient();
            if (!wlanClient.Interfaces.Any()) return false;
            var tmp = wlanClient.Interfaces.FirstOrDefault();
            return tmp != null &&
                   tmp.NetworkInterface.NetworkInterfaceType == NetworkInterfaceType.Wireless80211 &&
                   tmp.InterfaceState == Wlan.WlanInterfaceState.Connected;
        }

    }
}
