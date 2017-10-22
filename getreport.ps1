# Collection script

$jobid = 100
$hostname = hostname
$custdomain = (Get-WmiObject WIN32_ComputerSystem).Domain
$version  = Get-WmiObject -class Win32_OperatingSystem

$hotfixes = (Get-HotFix | select -Expand HotfixID) -join ","

$postdata = "hostname=$hostname.$custdomain&job_id=$jobid&hostdata="
$postdata += [uri]::EscapeDataString(($version.Version, $version.Caption, $hotfixes -join "|"))

$enc = [system.Text.Encoding]::UTF8
$Body = $enc.GetBytes($postdata)

[System.Net.ServicePointManager]::ServerCertificateValidationCallback = {$true}
$Request = [System.Net.HttpWebRequest]::Create('https://SERVER/grab/grab.php');
$Request.Method = 'POST';
$Request.ContentType = "application/x-www-form-urlencoded"
$Request.ContentLength = $Body.Length;
$Stream = $Request.GetRequestStream();
$Stream.Write($Body, 0, $Body.length);
$Request.GetResponse();
$Stream.Flush()
$Stream.Close()
$Request.Abort()
