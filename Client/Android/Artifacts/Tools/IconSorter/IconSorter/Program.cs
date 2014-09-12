using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IconSorter
{
    class Program
    {
        static void Main(string[] args)
        {
            Sorter sortet=new Sorter();
            sortet.Sort();

            Console.WriteLine("Success");
            Console.ReadKey(false);
        }
    }
}
