using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace duta_deskopt
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private duta_deskopt.MessangerBox box;
        private List<GetContactListResult> contactList;
        private DutaServices ds;
        private LoginResult me;

        public MainWindow()
        {
            InitializeComponent();

            ds = new DutaServices();
            me = ds.Login("user_c", "pass");
            myState.SelectedIndex = 0;
            contactList = ds.getContactList();
            generateContactList(contactList);
            
            
        }

        private void generateContactList(List<GetContactListResult> contactList) 
        {
            foreach (GetContactListResult contact in contactList)
            {
                contacts.Items.Add(generateContact(contact));
            }          
        }

        private ListBoxItem generateContact(GetContactListResult contact)
        {
            ListBoxItem item = new ListBoxItem();
            Grid grid = new Grid();

            grid.RowDefinitions.Add(new RowDefinition { Height = new GridLength(1, GridUnitType.Star) });
            grid.RowDefinitions.Add(new RowDefinition { Height = new GridLength(1, GridUnitType.Star) });
            grid.ColumnDefinitions.Add(new ColumnDefinition { Width = new GridLength(1, GridUnitType.Star) });
            grid.ColumnDefinitions.Add(new ColumnDefinition { Width = new GridLength(1, GridUnitType.Star) });

            item.Selected += new RoutedEventHandler(item_Selected);
            item.Name = contact.nickname;

            Label state = new Label();
            state.Content = contact.status;
            Grid.SetColumn(state, 0);
            Grid.SetRowSpan(state, 2);
            grid.Children.Add(state);

            Label user = new Label();
            user.Content = contact.nickname;
            Grid.SetColumn(user, 1);
            grid.Children.Add(user);

            Label desc = new Label();
            desc.Content = contact.description;
            Grid.SetColumn(desc, 1);
            Grid.SetRow(desc, 1);
            
            grid.Children.Add(desc);
            item.Content = grid;

            return item;
        }

        private void item_Selected(object sender, RoutedEventArgs e)
        {
            ListBoxItem item = (ListBoxItem)sender;
            if (box == null)
            {
                box = new MessangerBox(item.Name.ToString(), contactList,ds,me.user_id);
                box.Show();
            }
            else {
                box.newTab(item.Name.ToString());
               
            }
            
            
        }

        public void descChange_Click(object sender, RoutedEventArgs e)
        {
            ComboBoxItem state = (ComboBoxItem)myState.SelectedItem;
            int idState = Convert.ToInt32((string)state.Tag);
            ds.setStatus(idState, description.Text);
        }
    }   
}
