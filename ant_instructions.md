# Ant instructions

This guide explains how to set up `ant` and `xjc` to work:

- inside eclipse
- from cmd on windows

The two procedures are independent. If you want only to use ant inside eclipse, you don't need the second part (and viceversa).

**Warning**: ant wants to work (the project folder) in a directory whose path does not contain spaces

## Ant inside eclipse

### Prerequisites (eclipse)

You need the following installed on your machine:

- JDK
- Eclipse

Other prerequisites:

- Having a JAVA project in the workspace (the symbol J on the root folder of the project)
- Having a `build.xml` file

### Setting up eclipse

Right click on `build.xml` "Run as" -> "External Tools Configurations". Then:

- on tab JRE
  - select "Separate JRE"
  - click on "Installed JREs..."
    - "Add..."
      - "Standard VM" then "Next"
      - Select "Directory..." and choose the JDK location (something similar to "C:\Program Files\Java\jdk1.x.x")
      - "Finish"
    - Select the JDK ad default (tick)
    - "OK"
  - From the dropdown of "Separate JRE" select the JDK
- on tab "Common"
  - "Encoding" -> "Other" -> "ISO-8859-1"

## Ant from command line

### Prerequisites

You need the following installed on your machine:

- JDK in a known folder (similar to "C:\Program Files\Java\jdk1.x.x", from now on `{java_dir}`)

Download the following archives

- ant [Download zip](http://ant.apache.org/bindownload.cgi)
- jaxb-ri [Download zip](https://jaxb.java.net/latest/download.html)

### Procedure

Extract the two zips in a known folder (from now on identified by `{ant_dir}` and `{jaxb_ri_dir}`).

Set up the `PATH` environment variable to include the ant, xjc and schemagen `.bat` files (contained in `bin` folder).

Set up the following environment variables:

- `ANT_HOME` to `{ant_dir}`
- `JAVA_HOME` to `{java_dir}`
- `JAXB_HOME` to `{jaxb_ri_dir}`

### Additional info

To include the `.bat` files in the `PATH`, you can create a single folder and create inside it some symlinks. Symlinks can be created with the following command:

`mklink {tool_name}.bat {tool_dir}\bin\{tool_name}.bat` where `{tool_name}` stands for `ant`, `xjc` or `schemagen` and `{tool_dir}` is the path to the folder where the corresponding tool zip has been extracted.

In this way, a single folder needs to be added to the `PATH` environment variable.
