import re
import sys

if len(sys.argv) != 3:
    print('Wrong usage. Call ' + sys.argv[0] + ' <input_file> <output_file>')
    sys.exit()

fin = open(sys.argv[1], 'r')
fout = open(sys.argv[2], 'w')

content = fin.readlines()

for s in content:
    line =  '\n'.join(line.strip() for line in re.findall(r'.{1,80}(?:\s+|$)', s))
    fout.write(line + '\n')

print ('file has been line-wrapped. Output in file ' + sys.argv[2])
