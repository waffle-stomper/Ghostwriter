import subprocess, os

rootdir = os.getcwd()

print('removing old jars in the root dir...')
subprocess.call(['del','*.jar'], shell=True)

print('cleaning build directory...')
os.chdir(rootdir)
os.chdir('forge')
subprocess.call(['gradlew.bat','clean'])

print('building...')
os.chdir(rootdir)
os.chdir('forge')
subprocess.call(['gradlew.bat','build'])

print('copying most recent jar to root...')
os.chdir('build\libs')
files = sorted(os.listdir(os.curdir), reverse=True)
targetfile = files[0]
subprocess.call(['copy',targetfile,rootdir], shell=True)

