# ScanRegex

This application scans google and bing in search for a regex. It will automatically navigate through all search results and report the unique entries.<br><br>

The parametres are:<br>
[1] The address for the query on Google. E.g. https://www.google.com/search?q=site%3Alegistar.com <br>
[2] The address for the query on Bing. E.g. https://www.bing.com/search?q=site%3Alegistar.com <br>
[3] The regex. E.g. [\\w]+[.]legistar.com <br>
[4] Average Waiting time. Number of seconds that the program will wait between checking the 'next' results page. This is because Google will block your IP if you send too many queries too fast. <br>
