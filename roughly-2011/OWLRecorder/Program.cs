using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using TheOwl;

namespace OWLRecorder
{
    static class Program
    {
        /*private static string mysql_user = "owl";
        private static string mysql_pass = "hB7p42WsfF";
        private static string mysql_db = "owl";
        private static string mysql_server = "10.4.1.9";*/
        static TcpListener listener;
        static List<TcpClient> nsList = new List<TcpClient>();
        static System.Windows.Forms.Timer timer;
        static EnergyMonitor owl;

        const float A_TO_KW = 230 / 1000;

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            timer = new System.Windows.Forms.Timer();
            timer.Interval = 2000; // Interval for sending data;
            timer.Tick += new EventHandler(timer_Tick);
            timer.Start();

            Console.Write("Initialising OWL...");
            owl = new EnergyMonitor();
            owl.Init();
            Console.WriteLine(" Done.");
            Thread.Sleep(2000);

            while (!owl.IsAvailable())
            {
                Console.WriteLine("Waiting for OWL...");
                Thread.Sleep(2000);
            }

            // Handle Cancel to free owl.
            Console.CancelKeyPress += new ConsoleCancelEventHandler(Console_CancelKeyPress);

            listener = new TcpListener(IPAddress.Any, 10502);
            listener.Start();
            Console.WriteLine("Listening for new connection on 10502...");
            listener.BeginAcceptTcpClient(new AsyncCallback(AcceptClient), null);

            Application.Run();
        }

        static void Console_CancelKeyPress(object sender, ConsoleCancelEventArgs e)
        {
            Console.WriteLine("Exiting...");
            owl.Free();
            Console.WriteLine("Done!");
            Thread.Sleep(1000);

            Application.Exit();
        }

        static UTF8Encoding utf = new UTF8Encoding();

        static byte[] strNull = utf.GetBytes("N;");

        static void timer_Tick(object sender, EventArgs e)
        {
            Sensor[] results = owl.Query();
            Sensor result = results[0]; // In case none are valid
            
            for (int i = 0; i < results.Length; i++)
            {
                if(results[i].valid)
                    result = results[i];
            }
            string str = result.currentAmps * A_TO_KW + "," + result.accumulatedAmps + ";";
            byte[] strBytes = utf.GetBytes(str);

            foreach (TcpClient client in nsList)
            {
                if (!client.Connected)
                {
                    Console.WriteLine("Client ID " + nsList.IndexOf(client) + " is disconnected.");
                    nsList.Remove(client);
                    timer_Tick(null, null); // Redo loop since error otherwise.
                    break;
                }
                else
                {
                    try
                    {
                        if (result.valid)
                            client.GetStream().BeginWrite(strBytes, 0, strBytes.Length, clientWriteCallback, nsList.IndexOf(client));
                        else
                            client.GetStream().BeginWrite(strNull, 0, strNull.Length, clientWriteCallback, nsList.IndexOf(client));
                    }
                    catch (IOException)
                    {
                        client.Close();
                    }
                    catch (Exception exc)
                    {
                        Console.WriteLine("Error writing data to socket: " + exc.Message);
                    }
                    try
                    {
                        if (client.GetStream().DataAvailable)
                        {
                            // Close socket if anything is sent over connection. This is used for simple scripting on Linux
                            client.GetStream().Close();
                            client.Close();
                        }
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine("Error checking for stream data: " + ex.Message);
                    }
                }
            }

            Console.WriteLine((result.valid ? "Valid" : "Invalid!") + ", on port " + result.addr + ", model: " + result.model + ", Amps: " + result.currentAmps + ", AccAmps: " + result.accumulatedAmps);
        }

        static AsyncCallback clientWriteCallback = new AsyncCallback(ClientWriteFinish);

        private static void ClientWriteFinish(IAsyncResult result)
        {
            nsList[(Int32)result.AsyncState].GetStream().EndWrite(result);
            nsList[(Int32)result.AsyncState].GetStream().Flush();
        }

        private static void AcceptClient(IAsyncResult result)
        {
            TcpClient client = listener.EndAcceptTcpClient(result);
            client.NoDelay = true;
            nsList.Add(client);
            Console.WriteLine("Client ID " + nsList.IndexOf(client) + " is connected.");

            // Listen for new connection.
            listener.BeginAcceptTcpClient(new AsyncCallback(AcceptClient), null);
            Console.WriteLine("Listening for new connection on 10502...");
        }
    }
}
