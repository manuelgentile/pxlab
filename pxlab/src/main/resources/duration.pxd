 Experiment(){

    Context(){

        AssignmentGroup(){
			DataFileDestination= ".";
            TrialFactor = 10;
            SubjectCode = "pxlab";
            new st = de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER;
            new dt = 6;

        }
        Session(){

            TextParagraph(){

                Text = ["Video Frame Duration Limit Test", " ", "Use parameter \'dt\' to set an intended duration.", "Actual duration should be an integer multiple of the video frame duration.", "Use options \'-S4 -Rnnn -wnnn -hnnn \' to set video system resolution and refresh rate.", "Use option \'-D time\' to get detailed timing information.", "Timing data are stored in \'pxlab.dat\'.", " ", "Press any key to start."];
                FontSize = 24;

            }
            ClearScreen(){

                Timer = de.pxlab.pxl.TimerCodes.NO_TIMER;

            }

        }
        Trial( SimpleBar.Duration, SimpleBar.TimeControl, ClearScreen:Post.TimeControl, SimpleBar.ResponseTime, SimpleBar.TimeError){

            ClearScreen:Pre(){

                Timer = de.pxlab.pxl.TimerCodes.CLOCK_TIMER;
                Duration = 200;

            }
            SimpleBar(){

                Timer = st;
                Duration = dt;
                Height = screenHeight();
                Width = 400;
                Color = White;

            }
            ClearScreen:Post(){

                Timer = st;
                Duration = 1;

            }

        }
        // Command line assignments
        AssignmentGroup();

    }
    Procedure(){

        Session(){

            Block(){

                Trial( ?, ?, ?, ?, ?);

            }

        }

    }

}
