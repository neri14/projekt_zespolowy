using System;
using System.Collections.Generic;
using System.Linq;
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
        public MainWindow()
        {
            InitializeComponent();
            generateContactList();
        }

        private void generateContactList() 
        {


            contacts.Items.Add(generateContact());

        }

        private ListBoxItem generateContact()
        {
            ListBoxItem item = new ListBoxItem();
            Grid grid = new Grid();

            grid.RowDefinitions.Add(new RowDefinition { Height = new GridLength(1, GridUnitType.Star) });
            grid.RowDefinitions.Add(new RowDefinition { Height = new GridLength(1, GridUnitType.Star) });
            grid.ColumnDefinitions.Add(new ColumnDefinition { Width = new GridLength(1, GridUnitType.Star) });
            grid.ColumnDefinitions.Add(new ColumnDefinition { Width = new GridLength(1, GridUnitType.Star) });

            Label state = new Label();
            state.Content = "status";
            Grid.SetColumn(state, 0);
            Grid.SetRowSpan(state, 2);
            grid.Children.Add(state);

            Label user = new Label();
            user.Content = "uzytkownik";
            Grid.SetColumn(user, 1);
            grid.Children.Add(user);

            Label desc = new Label();
            desc.Content = "opis";
            Grid.SetColumn(desc, 1);
            Grid.SetRow(desc, 1);
            
            grid.Children.Add(desc);
            item.Content = grid;

            return item;
        }
    }   
}
