import subprocess, os, shutil, sys, time



thisdir = os.getcwd()
print("This dir: " + thisdir)

# Extract the version number from build.gradle
forge_version = ""
with open("forge\\build.gradle", "r") as infile:
    for line in infile.readlines():
        if line.startswith("archivesBaseName"):
            forge_startdex = line.index('"')+1
            forge_endex = line.index('-')
            forge_version = line[forge_startdex:forge_endex]

mcmodsdir = os.path.join(os.getenv('APPDATA'), ".minecraft", "mods",
                         forge_version)
print("Mods dir: " + mcmodsdir)

fromitem = ""
frombasename = ""
fromitempath = ""
for fromitem in os.listdir(thisdir):
    fromitempath = os.path.join(thisdir, fromitem)
    if os.path.isfile(fromitempath):
        if (fromitem.endswith(".jar")):
            # Got our jar!
            print("Found source jar: " + fromitem)
            startdex = fromitem.find("-")+1
            endex = fromitem.find("-", startdex)
            frombasename = fromitem[startdex:endex]
            break

if frombasename == "" or fromitempath == "":
    print("Couldn't get a source jar. Exiting")
    input("Press enter to continue...")
    sys.exit(-1)

moveflag = False
toitem = ""
toitempath = ""
for toitem in os.listdir(mcmodsdir):
    toitempath = os.path.join(mcmodsdir, toitem)
    if os.path.isfile(toitempath):
        if toitem.endswith(".jar"):
            startdex = toitem.find("-")+1
            endex = toitem.find("-", startdex)
            tobasename = toitem[startdex:endex]
            print(tobasename)
            if tobasename == frombasename:
                print("It looks like we've found a candidate to replace!")
                print(toitempath)
                moveflag = True
                break

if not moveflag:
    print("Couldn't find a jar to replace. Exiting")
    input("Press enter to continue...")
    sys.exit(-1)

print("Renaming old jar...")
curtime = int(time.time())
shutil.move(toitempath, "{}_disabled_{}".format(toitempath, curtime))
print("Copying new jar...")
shutil.copy(fromitempath, mcmodsdir)
input("Done! Press any key to continue...")




