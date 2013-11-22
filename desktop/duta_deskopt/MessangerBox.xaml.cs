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
        int me_id;

        public MessangerBox(String UserName, List<GetContactListResult> contactList, DutaServices ds, int me_id)
        {
            InitializeComponent();
            this.ds = ds;
            this.contactList = contactList;
            this.me_id = me_id;
            Thread trd = new Thread(new ThreadStart(this.messageThread));
            trd.IsBackground = true;
            trd.Start();
            newTab(UserName);
        }

        private Grid buildTemplate(String UserName)
        {
            Grid grid = new Grid();

            grid.RowDefinitions.Add(new RowDefinition { Height = new GridLength(1, GridUnitType.Star) });
            grid.RowDefinitions.Add(new RowDefinition { Height = new GridLength(1, GridUnitType.Star) });
            grid.ColumnDefinitions.Add(new ColumnDefinition { Width = new GridLength(1, GridUnitType.Star) });

            TextBlock textBlock = new TextBlock();
            textBlock.Name = "tb_" + UserName;
            Grid.SetColumn(textBlock, 0);
            Grid.SetRow(textBlock, 0);
            grid.Children.Add(textBlock);

            RichTextBox richTextBox = new RichTextBox();
            richTextBox.Name = "rtb_" + UserName;
            richTextBox.KeyUp += new KeyEventHandler(text_enter);
            Grid.SetColumn(richTextBox, 0);
            Grid.SetRow(richTextBox, 1);
            grid.Children.Add(richTextBox);
            return grid;
        }

        public void newTab(String UserName)
        {
            TabItem tab = new TabItem();
            tab.Header = UserName;
            tab.Name = UserName;
            tab.Content = buildTemplate(UserName);
            TabsCon.Items.Add(tab);
        }

        public void text_enter(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Enter)
            {
                RichTextBox rtb = (RichTextBox)sender;
                String userName = rtb.Name.Substring(4);
                String textBlockName = "tb_" + userName;
                Grid grid = (Grid)rtb.Parent;
                TextBlock tb = new TextBlock();
                
                for (int i = 0; i < VisualTreeHelper.GetChildrenCount(grid); ++i)
                {
                    var child = VisualTreeHelper.GetChild(grid, i) as FrameworkElement;
                    string name = child.Name;
                    if (child != null && child.Name == userName)
                    {
                        tb = (TextBlock)child;
                    }
                }

                TextRange textRange = new TextRange(rtb.Document.ContentStart, rtb.Document.ContentEnd);

                int idUser = getUserID(userName);
                ds.sendMessage(me_id,idUser, textRange.Text);

                tb.Text = tb.Text + System.Environment.NewLine + textRange.Text;
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

        private void messageThread(){
            while (true)
            {
                GetMessageResponse_Message messange = ds.getMessange();
                String teskt = messange.message;
                
            }
        }
    }
}
