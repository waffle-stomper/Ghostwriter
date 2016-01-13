from subprocess import call, Popen
from shutil import move


def increment(line):
    linedex = line.index('-')
    majdot = line.index('.', linedex+1)
    majnum = int(line[linedex+1:majdot])

    mindot = line.index('.',majdot+1)
    minnum = int(line[majdot+1:mindot])

    revnum = int(line[mindot+1:-2]) + 1

    if revnum == 10:
        revnum = 0
        minnum += 1
        if minnum == 10:
            minnum = 0
            majnum +=1
            
    versionstring = "{}.{}.{}\"".format(majnum, minnum, revnum)
    print("Incrementing version to {}.{}.{}".format(majnum, minnum, revnum))
    newline = line[:linedex+1] + versionstring + "\n"
    fullversion = line[linedex-5:linedex+1] + versionstring[:-1]
    
    return newline, versionstring + ";\n", fullversion


#Get the base name and upgrade version in build.gradle 
print("Getting mod name and incrementing version in build.gradle...")
version_found = False
basename = ""
newversion = ""
fullversion = ""
with open("forge\\build.gradle.tmp", "w") as outfile:
    with open("forge\\build.gradle", "r") as infile:
        for line in infile.readlines():
            if line.startswith("version"):
                if not version_found:
                    version_found = True
                    line, newversion, fullversion = increment(line)
            elif line.startswith("archivesBaseName"):
                basename = line[20:-2]
            outfile.write(line)

print("Base name: {}".format(basename))


#Copy build.gradle.tmp to build.gradle
move("forge\\build.gradle.tmp", "forge\\build.gradle")


#Update the version number in the main class
classpath = "forge\\src\\main\\java\\wafflestomper\\{}\\{}.java".format(basename.lower(), basename)
print("Inserting new version into main class...")
with open(classpath + ".tmp", "w") as outfile:
    with open(classpath, "r") as infile:
        for line in infile.readlines():
            if line.strip().startswith("public static final String VERSION"):
                line = line[:line.index('-')+1] + newversion 
            outfile.write(line)

move(classpath + ".tmp", classpath)

#Add the new version number to the changelog
print("Updating the changelog...")
with open("forge\\changelog.txt.tmp", "w") as outfile:
    with open("forge\\changelog.txt", "r") as infile:
        outfile.write("{}:\n - \n\n".format(fullversion))
        for line in infile.readlines():
            outfile.write(line)
move("forge\\changelog.txt.tmp", "forge\\changelog.txt")
