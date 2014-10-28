using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;

namespace IconSorter
{
    public class Sorter
    {
        private readonly List<string> _density = new List<string> {"ldpi", "mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"};
        private readonly Dictionary<string, string> _sizeToDensitiesMap;

        private readonly string _sourse;
        private readonly string _destination;

        public Sorter()
        {
            _sizeToDensitiesMap = new Dictionary<string, string>();
            foreach (var key in _density)
            {
                _sizeToDensitiesMap.Add(ConfigurationManager.AppSettings.Get(key), key);
            }

            _sourse = ConfigurationManager.AppSettings.Get("Sourse");
            _destination = ConfigurationManager.AppSettings.Get("Destination");

        }

        public void Sort()
        {
            var directory = new DirectoryInfo(_sourse);
            FileInfo[] pngs = directory.GetFiles("*.png", SearchOption.AllDirectories)
                .Where(x => x.Name != "artwork-source.png").ToArray();
            foreach (var fileInfo in pngs)
            {
                ProcessFile(fileInfo);
            }
        }

        private void ProcessFile(FileInfo fileInfo)
        {
            string sourseFileName = fileInfo.Name;
            string extension = fileInfo.Extension;
            string nameWithoutExtension = sourseFileName.Substring(0, sourseFileName.Length - extension.Length);
            string[] nameParts = nameWithoutExtension.Split('-');
            string fileName = string.Format("{0}{1}", nameParts[0], extension);
            string sizeInfo = nameParts[1];
            CopyFile(fileInfo, fileName, sizeInfo);
        }

        private void CopyFile(FileInfo fileInfo, string fileName, string sizeInfo)
        {
            string density = _sizeToDensitiesMap[sizeInfo];
            CopyTo(fileInfo.FullName, fileName, density, _destination);

            if (density == "hdpi")
            {
                CopyTo(fileInfo.FullName, fileName, _destination);
            }
        }

        private void CopyTo(string originalPath, string fileName, string density, string destination)
        {
            string newPath = Path.Combine(destination, string.Format("drawable-{0}", density));
            CopyToOutput(originalPath, fileName, newPath);
        }

        private void CopyTo(string originalPath, string fileName, string destination)
        {
            string newPath = Path.Combine(destination, string.Format("drawable"));
            CopyToOutput(originalPath, fileName, newPath);
        }

        private static void CopyToOutput(string originalPath, string fileName, string newPath)
        {
            var directoryDestination = new DirectoryInfo(newPath);
            if (!directoryDestination.Exists)
            {
                directoryDestination.Create();
            }
            string newName = Path.Combine(directoryDestination.FullName, fileName);
            File.Copy(originalPath, newName, true);
        }
    }
}