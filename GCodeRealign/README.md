# GCodeRealign

A geometric constraint solver. It allows to define relationsships between the elements (lines and arcs) of a contour and solve it.

# Installation

* Download from: [http://plugins.netbeans.org/plugin/68344/](http://plugins.netbeans.org/plugin/68344/ )
* Go to "Tools" -> "Plugins" -> "Downloaded", click "Add Plugins..." and select the downloaded file org-roiderh-gcoderealign.nbm
* Check the Checkbox and click "Install"

# Usage

Select a pice of g-code which describes a contour. Click the Toolbar button ![Toolbarbutton to open the Solver](src/org/roiderh/gcoderealign/hi22-gcode-realign.png ).

![Selected g-code which describes the contour with form for constraints](GcodeRealign_screen_before.png )

Define the relationsships of the contour elements like fixed points and angles and click "calculate".
Click "ok" to replace selected contour by the new calculated contour.

![Already solved the contour](GcodeRealign_screen_after.png )

click "ok" to replace the selected G-code with the new solved G-code.

