using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace RSA_Crypto
{
    public partial class ManageKeys : Form
    {
        public StringCollection sc = new StringCollection();
        
        public ManageKeys()
        {
            InitializeComponent();
            for (int i = 0; i < sc.Count; i++)
            {
                ListBox.Items.Add(sc[i]);
            }
        }

        private void Delete_Button_Click(object sender, EventArgs e)
        {

        }
    }
}
