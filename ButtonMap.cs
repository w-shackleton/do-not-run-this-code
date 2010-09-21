using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace DroidPad
{
    public partial class ButtonMap : Form
    {
        private const int NUM_A = 8;
        private const int NUM_D = 16;
        // Ints are stored as strings, since stored in settings
        /// <summary>
        /// List of buttons and axes from PPJoy
        /// </summary>
        private static string[] buttons = null;
        //"x y z zrot slider xrot yrot dial "

        DataTable leftTable, rightTable;

        /// <summary>
        /// Initialises data for the first time
        /// </summary>
        public static void Initialise()
        {
            buttons = new string[NUM_D + NUM_A];
            buttons[0] = "X Axis";
            buttons[1] = "Y Axis";
            buttons[2] = "Z Axis";
            buttons[3] = "Z rotation";
            buttons[4] = "Slider";
            buttons[5] = "X rotation";
            buttons[6] = "Y Rotation";
            buttons[7] = "Dial";
            for (int i = 0; i < buttons.Length - NUM_A; i++)
            {
                buttons[i + NUM_A] = "Button " + (i + 1);
            }

            if (Properties.Settings.Default.buttonMaps == null)
                Properties.Settings.Default.buttonMaps = new System.Collections.Specialized.StringCollection();
            if (Properties.Settings.Default.buttonActiveMaps == null)
                Properties.Settings.Default.buttonActiveMaps = new System.Collections.Specialized.StringCollection();

            if (Properties.Settings.Default.buttonMaps.Count != buttons.Length)
            {
                Properties.Settings.Default.buttonMaps.Clear();
                for (int i = 0; i < buttons.Length; i++)
                    Properties.Settings.Default.buttonMaps.Add("-1");
            }
            if (Properties.Settings.Default.buttonActiveMaps.Count != buttons.Length)
            {
                Properties.Settings.Default.buttonActiveMaps.Clear();
                for (int i = 0; i < buttons.Length; i++)
                    Properties.Settings.Default.buttonActiveMaps.Add(Convert.ToString(i));
            }
        }
        
        public ButtonMap()
        {
            InitializeComponent();

            leftTable = new DataTable();
            leftTable.Columns.Add("Name", typeof(string));
            leftTable.Columns.Add("Mapping", typeof(string));

            rightTable = new DataTable();
            rightTable.Columns.Add("Name", typeof(string));

            dataGridLeft.DataSource = leftTable;
            dataGridRight.DataSource = rightTable;
            updateButtonMaps();
        }

        private void reset_Click(object sender, EventArgs e)
        {
            Properties.Settings.Default.buttonMaps.Clear();
            Properties.Settings.Default.buttonActiveMaps.Clear();
            for (int i = 0; i < buttons.Length; i++)
                Properties.Settings.Default.buttonActiveMaps.Add(Convert.ToString(i));
            for (int i = 0; i < buttons.Length; i++)
                Properties.Settings.Default.buttonMaps.Add("-1");
            updateButtonMaps();
        }

        private void clear_Click(object sender, EventArgs e)
        {
            Properties.Settings.Default.buttonMaps.Clear();
            Properties.Settings.Default.buttonActiveMaps.Clear();
            for (int i = 0; i < buttons.Length; i++)
                Properties.Settings.Default.buttonMaps.Add(Convert.ToString(i));
            for (int i = 0; i < buttons.Length; i++)
                Properties.Settings.Default.buttonActiveMaps.Add("-1");
            updateButtonMaps();
        }

        private void updateButtonMaps()
        {
            /*// Keep selections
            DataGridViewSelectedRowCollection cL = dataGridLeft.SelectedRows;
            DataGridViewSelectedRowCollection cR = dataGridRight.SelectedRows;*/
            rightTable.Clear();
            int i = 0;
            foreach (string name in Properties.Settings.Default.buttonMaps)
            {
                int num = Convert.ToInt32(name);
                if (num == -1)
                    rightTable.Rows.Add(new string[] { "" });
                else
                    rightTable.Rows.Add(new string[] { buttons[Convert.ToInt32(name)] });
            }

            leftTable.Clear();
            i = 0;
            foreach (string name in Properties.Settings.Default.buttonActiveMaps)
            {
                try
                {
                    int num = Convert.ToInt32(name);
                    if (num == -1)
                        leftTable.Rows.Add(new string[] { buttons[i++], "" });
                    else
                        leftTable.Rows.Add(new string[] { buttons[i++], buttons[num] });
                }
                catch // Could be a multi-button axis?
                {
                    if (name.Contains(";")) // It is a multi-button axis
                    {
                        string[] nums = name.Split(';');
                        int num0 = Convert.ToInt32(nums[0]);
                        int num1 = Convert.ToInt32(nums[1]);
                        leftTable.Rows.Add(new string[] { buttons[i++], buttons[num0] + ", " + buttons[num1] });
                    }
                }
            }
        }

        private void updateAll()
        {
            updateButtonMaps();
            dataGridLeft_SelectionChanged(null, null);
        }

        private void dataGridLeft_SelectionChanged(object sender, EventArgs e)
        {
            buttonInsert.Enabled = checkInsertEnabled();
            buttonRemove.Enabled = checkRemoveEnabled();
        }

        private void dataGridRight_SelectionChanged(object sender, EventArgs e)
        {
            dataGridLeft_SelectionChanged(sender, e); // Use same callback for both
        }

        /// <summary>
        /// Checks whether the remove button should be enabled
        /// </summary>
        /// <returns></returns>
        private bool checkRemoveEnabled()
        {
            if (dataGridLeft.SelectedRows.Count != 1) return false;
            if (Properties.Settings.Default.buttonActiveMaps[dataGridLeft.SelectedRows[0].Index] == "-1")
                return false;
            return true;
        }

        /// <summary>
        /// Checks whether the insert button should be enabled
        /// </summary>
        /// <returns></returns>
        private bool checkInsertEnabled()
        {
            if (dataGridLeft.SelectedRows.Count != 1) return false;
            if (Properties.Settings.Default.buttonActiveMaps[dataGridLeft.SelectedRows[0].Index] != "-1")
                return false;
            if (dataGridLeft.SelectedRows[0].Index < NUM_A) // If it is analogue
            {
                if (dataGridRight.SelectedRows.Count == 1)
                {
                    if (Convert.ToInt32(Properties.Settings.Default.buttonMaps[dataGridRight.SelectedRows[0].Index]) < NUM_A &&
                        Convert.ToInt32(Properties.Settings.Default.buttonMaps[dataGridRight.SelectedRows[0].Index]) != -1) // If RH is also analogue
                        return true;
                }
                else if (dataGridRight.SelectedRows.Count == 2)
                {
                    if (Convert.ToInt32(Properties.Settings.Default.buttonMaps[dataGridRight.SelectedRows[0].Index]) >= NUM_A &&
                        Convert.ToInt32(Properties.Settings.Default.buttonMaps[dataGridRight.SelectedRows[0].Index]) != -1 &&
                        Convert.ToInt32(Properties.Settings.Default.buttonMaps[dataGridRight.SelectedRows[1].Index]) >= NUM_A &&
                        Convert.ToInt32(Properties.Settings.Default.buttonMaps[dataGridRight.SelectedRows[1].Index]) != -1)
                        return true;
                }
            }
            else // If it is digital
            {
                if (dataGridRight.SelectedRows.Count == 1)
                    if (Convert.ToInt32(Properties.Settings.Default.buttonMaps[dataGridRight.SelectedRows[0].Index]) >= NUM_A &&
                        Convert.ToInt32(Properties.Settings.Default.buttonMaps[dataGridRight.SelectedRows[0].Index]) != -1) // If RH is also digital
                        return true;
            }
            return false;
        }

        private void buttonRemove_Click(object sender, EventArgs e)
        {
            try
            {
                int retButtonNum = Convert.ToInt32(Properties.Settings.Default.buttonActiveMaps[dataGridLeft.SelectedRows[0].Index]);
                Properties.Settings.Default.buttonMaps[retButtonNum] = Convert.ToString(retButtonNum);

                Properties.Settings.Default.buttonActiveMaps[dataGridLeft.SelectedRows[0].Index] = "-1";
            }
            catch // Could be a multi-button axis?
            {
                string name = Properties.Settings.Default.buttonActiveMaps[dataGridLeft.SelectedRows[0].Index];
                if (name.Contains(";")) // It is a multi-button axis
                {
                    string[] nums = name.Split(';');
                    int num0 = Convert.ToInt32(nums[0]);
                    int num1 = Convert.ToInt32(nums[1]);

                    Properties.Settings.Default.buttonMaps[num0] = Convert.ToString(num0);
                    Properties.Settings.Default.buttonMaps[num1] = Convert.ToString(num1);

                    Properties.Settings.Default.buttonActiveMaps[dataGridLeft.SelectedRows[0].Index] = "-1";
                }
            }

            updateAll();
        }

        private void buttonInsert_Click(object sender, EventArgs e)
        {
            if (dataGridRight.SelectedRows.Count == 1)
            {
                int retButtonNum = dataGridRight.SelectedRows[0].Index;
                Properties.Settings.Default.buttonActiveMaps[dataGridLeft.SelectedRows[0].Index] = Convert.ToString(retButtonNum);

                Properties.Settings.Default.buttonMaps[dataGridRight.SelectedRows[0].Index] = "-1";
            }
            else // if (dataGridRight.SelectedRows.Count == 2) // this is the only other case
            {
                int num0 = dataGridRight.SelectedRows[0].Index;
                int num1 = dataGridRight.SelectedRows[1].Index;

                Properties.Settings.Default.buttonActiveMaps[dataGridLeft.SelectedRows[0].Index] = num0 + ";" + num1;

                Properties.Settings.Default.buttonMaps[num0] = "-1";
                Properties.Settings.Default.buttonMaps[num1] = "-1";
            }

            updateAll();
        }

        private void dataGridLeft_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {

        }

        public class MapData
        {
            public MapData(int origNum, int origData)
            {
                if (!Properties.Settings.Default.buttonActiveMaps[origNum].Contains(";"))
                {
                    data = origData;
                    num = Convert.ToInt32(Properties.Settings.Default.buttonActiveMaps[origNum]);
                }
                else
                {
                    analogue = false;
                    twoDigital = true;
                    string[] nums = Properties.Settings.Default.buttonActiveMaps[origNum].Split(';');
                    d1 = Convert.ToInt32(nums[0]) - NUM_A;
                    d2 = Convert.ToInt32(nums[1]) - NUM_A;

                    dd1 = origData > 8192 * 3 - 2000;
                    dd2 = origData < 8192 * 1 + 2000;
                }
            }
            public MapData(int origNum, bool origData)
            {
                analogue = false;
                d1 = Convert.ToInt32(Properties.Settings.Default.buttonActiveMaps[origNum + NUM_A]) - NUM_A;
                dd1 = origData;
            }

            public int num, data;
            public bool analogue = true;

            public bool twoDigital = false;

            public int d1, d2;
            public bool dd1, dd2;
        }
    }
}
