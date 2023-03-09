These are the files accompanying the paper, "Reflections on kernelizing and computing unrooted agreement forests"
by Rim van Wersch, Steven Kelk, Simone Linz, and Georgios Stamoulis. (Update 20th August 2021: following the request of a reviewer we have added an .ods version
of our main experimental spreadsheet, supplementing the .xls version in the main .zip file).

* The two spreadsheets contain the raw, unprocessed experimental results for the two datasets considered in the article.

* The 735 (respectively, 90) tree pairs that constitute the main (respectively, large trees) dataset, can be found in the folder "maindataset" (respectively, "largetreedataset").
These files have the suffix ".tree". The two subfolders contain subtree-reduced (".sr") and fully-reduced (".newrules") variants of these trees.

* The folder "dmp_sampling_bound_code" contains self-contained source code for the sampling of convex characters, yielding strong lower bounds on dMP and thus dTBR. To compile
the code, you first need to run JavaCC (the Java Compiler Compiler) on the ".jj" file, and then compile the resulting ".java" files with a usual Java Compiler. To assist, we
have already merged the compiled files into a ".jar" file for you.

To get information on running this code, type:

java -jar ./ConvexBound.jar -help

* A copy of our kernelization+ILP solver Tubro is available on request.

Any questions, don't hesitate to ask.

Kind regards,

Steven Kelk
11th December 2020 


