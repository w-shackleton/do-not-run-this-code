<?php
print_r(gd_info());
echo "\n\n";
if (imagetypes() & IMG_GIF) echo "GIF Support is enabled\n";
if (imagetypes() & IMG_JPG) echo "JPEG Support is enabled\n";
if (imagetypes() & IMG_PNG) echo "PNG Support is enabled\n";
if (imagetypes() & IMG_WBMP) echo "WBMP Support is enabled\n";
?> 