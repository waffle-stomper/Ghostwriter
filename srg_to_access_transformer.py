# Converts output.srg to accessTransformer format

from typing import List
import re

FIELD_PATTERN = re.compile("FD: (?P<slash_path>.+)/(?P<srg_name>.+) .+/(?P<mcp_name>.+)")
METHOD_PATTERN = re.compile("MD: (?P<slash_path>.+)/(?P<srg_name>.+) (?P<descriptor>.+) .+/(?P<mcp_name>.+) .+")

def convert_line(line_in: str, level: str = "public", remove_final: bool = False) -> str:
    line_in = line_in.strip()
    if line_in.startswith("FD"):
        # Field line
        match = re.match(FIELD_PATTERN, line_in)
        dot_path = match.group("slash_path").replace("/", ".")
        srg_name = match.group("srg_name")
        mcp_name = match.group("mcp_name")
        return "{} {} {}  # {}\n".format(level, dot_path, srg_name, mcp_name)
    elif line_in.startswith("MD"):
        match = re.match(METHOD_PATTERN, line_in)
        dot_path = match.group("slash_path").replace("/", ".")
        srg_name = match.group("srg_name")
        descriptor = match.group("descriptor")
        mcp_name = match.group("mcp_name")
        disabler = "# " if "<" in srg_name else ""
        final_mod = "-f" if remove_final else ""
        return "{}{}{} {} {}{}  # {}()\n".format(disabler, level, final_mod, dot_path, srg_name, descriptor, mcp_name)
    else:
        return "# {}\n".format(line_in)


if __name__ == "__main__":
    output_lines: List[str] = list()
    with open("build/createSrgToMcp/output.srg", "r") as f:
        for i, line in enumerate(f.readlines()):
            if (i+1)%1000 == 0:
                print("Working on line {}".format(i+1))
            output_lines.append(convert_line(line))

    print("Writing output...")
    with open("access_transformer_lines.txt", "w") as f:
        f.writelines(output_lines)

    print("Done")
