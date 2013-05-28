"The third-party/ directory is a place for the optional package and extension
library JAR files that are required for your build.
[...]
Finally, and probably most importantly, is that it consolidates all of your
extension libraries for your project where it is easy to put them under
version control.  It seems a bit odd to put a third-party binary file under
version control when you can usually just download another copy from the
third-party website whenever you need it.  This is unreliable, however, as
there might be dependencies in the code that require you to use an older
version of a library that is no longer distributed from the vendor site.
When your party/ directory is under version control, you have a snapshot of
the versions of all the different extension libraries that are known to
work reliably with your project.  It also makes it easier on your fellow
developers, including you if you ever need to move to another development
machine.  You can pull the JAR files from the version control system
instead of downloading them all over again from multiple vendor websites."
  -- David Wallace Croft, Advanced Java Game Programming, 2004, pp6-7