using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace duta_deskopt
{
    /// <summary>
    /// Interaction logic for MessangeBox.xaml
    /// </summary>
    public partial class MessangerBox : Window
    {
        private DutaServices ds;
        private List<GetContactListResult> contactList;
        private List<TextBlock> textBlockList = new List<TextBlock>();
        int me_id;
        private Dictionary<String, List<int>> conferention = new Dictionary<string,List<int>>();

        public MessangerBox(String userName, List<GetContactListResult> contactList, DutaServices ds, int me_id,List<int> conferrentionList=null)
        {
            InitializeComponent();
            this.ds = ds;
            this.contactList = contactList;
            this.me_id = me_id;
            newTab(userName, conferrentionList);
        }

        

        private Grid buildTemplate(String UserName)
        {
            Grid grid = new Grid();

            grid.RowDefinitions.Add(new RowDefinition { Height = new GridLength(1, GridUnitType.Star) });
            grid.RowDefinitions.Add(new RowDefinition { Height = new GridLength(1, GridUnitType.Star) });
            grid.ColumnDefinitions.Add(new ColumnDefinition { Width = new GridLength(1, GridUnitType.Star) });

            ScrollViewer sv = new ScrollViewer();
            
            TextBlock textBlock = new TextBlock();
            textBlock.Name = "tb_" + UserName;
            Grid.SetColumn(textBlock, 0);
            Grid.SetRow(textBlock, 0);          
            sv.Content = textBlock;
            textBlockList.Add(textBlock);
            grid.Children.Add(sv);

            RichTextBox richTextBox = new RichTextBox();
            richTextBox.Name = "rtb_" + UserName;
            richTextBox.KeyUp += new KeyEventHandler(text_enter);
            Grid.SetColumn(richTextBox, 0);
            Grid.SetRow(richTextBox, 1);
            grid.Children.Add(richTextBox);
            return grid;
        }

        public void newTab(String userName, List<int> conferrentionList = null)
        {
            if (conferrentionList != null && userName == "Confferention")
            {
                userName = "Konferencja_" + (conferention.Count + 1);
                conferention.Add(userName, conferrentionList);
            }

            TabItem tab = new TabItem();
            tab.Header = userName;
            tab.Name = userName;
            tab.Content = buildTemplate(userName);
            TabsCon.Items.Add(tab);
        }

        public void text_enter(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Enter)
            {
                RichTextBox rtb = (RichTextBox)sender;
                String userName = rtb.Name.Substring(4);
                String textBlockName = "tb_" + userName;

                TextRange textRange = new TextRange(rtb.Document.ContentStart, rtb.Document.ContentEnd);
                List<int> users;
                if (userName.Length>11 && userName.Substring(0, 11) == "Konferencja")
                {
                    users = conferention[userName];
                }
                else 
                {
                    int idUser = getUserID(userName);
                    users = new List<int>();
                    users.Add(idUser);
                    
                }
                DateTime date = ds.sendMessage(me_id, users, textRange.Text);

                foreach (TextBlock tb in textBlockList)
                {
                    if (tb.Name == textBlockName)
                    {
                        
                        tb.Text = tb.Text + System.Environment.NewLine + "Ja - " + date.ToShortTimeString();
                        tb.Text = tb.Text + System.Environment.NewLine + textRange.Text;
                    }
                }
                rtb.Document.Blocks.Clear();

            }
            
        }

        private int getUserID(string userName)
        {
            foreach (GetContactListResult contact in contactList)
            {
                if (contact.nickname.Equals(userName))
                {
                    return contact.user_id;
                }
            }
            return -1;
        }

        public void addMessange(String text, String tab, String date, String author = null) {
            if (author == null) 
            {
                author = tab;
            }
            foreach (TextBlock tb in textBlockList) {
                if (tb.Name == "tb_" + tab) {
                    tb.Text = tb.Text + System.Environment.NewLine + author + " - " + date;
                    tb.Text = tb.Text + System.Environment.NewLine + text;
                }
            }
        }

        public bool isTab(String tabName) {
            foreach (TabItem tabItem in TabsCon.Items) {
                if (tabItem.Name == tabName) {
                    return true;
                }
            }

            return false;
        }

        public bool isConferention(List<int> conferrentionList)
        {
            bool flag = true;
            foreach (KeyValuePair<String, List<int>> pair in conferention) 
            {
                flag = true;
                for (int i = 0; i < conferrentionList.Count; i++)
                {
                    if (pair.Value[i] != conferrentionList[i])
                        flag = false;
                }
                if (flag == true)
                    return true;
            }

            return false ;
        }

        public String findConfferention(List<int> list_id) 
        {
            bool flag;
            foreach (KeyValuePair<String, List<int>> pair in conferention)
            {
                if (list_id.Count == pair.Value.Count)
                {
                    flag = true;
                    for (int i = 0; i < pair.Value.Count; i++) 
                    {
                        int find = list_id.Find(item => item==pair.Value[i] );
                        if (find == 0) 
                        {
                            flag = false;
                        }
                    }
                    if (flag == true)
                    {
                        return pair.Key;
                    }
                }
                else 
                {
                    flag = false;
                }
            }
            return "";
        }

    }
}
