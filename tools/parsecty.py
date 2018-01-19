#!/usr/bin/env python3
"""Parse a cty.dat file and convert it to our format

Usage: ./parsecty.py < cty.dat > ../app/src/main/res/raw/cty.txt

Country files may be downloaded from http://www.country-files.com/, from a
CTY-2703 release and then using old/cty.dat.
"""
import re
import sys

class ISOResolver:
    def __init__(self):
        self.mapping = {}

    def parse_file(self, the_file):
        for line in the_file:
            self.parse_line(line)

    def parse_line(self, line):
        itu, _, iso = line.strip().partition('\t')
        self.mapping[itu] = iso.split(';') if iso != '?' else ['_generic']

    def resolve(self, prefix):
        return self.mapping[prefix]

class Country:
    iso_resolver = ISOResolver()

    def __init__(self,
            name=None, cqzone=None, ituzone=None, primary_prefix=None,
            continent=None, aliases=None):
        if name is None or \
                cqzone is None or \
                ituzone is None or \
                primary_prefix is None or \
                continent is None:
            raise AttributeError('Supply Country with all parameters')
        self.name = name
        self.cqzone = cqzone
        self.ituzone = ituzone
        self.primary_prefix = primary_prefix
        self.continent = continent
        self.isocodes = Country.iso_resolver.resolve(primary_prefix)
        self.aliases = aliases if aliases is not None else []

    def add_alias(self, alias):
        self.aliases.append(alias)

    def __str__(self):
        return '%s\n>%s;%s;%s;%s' % (
                '\n'.join(self.aliases),
                self.name,
                '-'.join(self.isocodes),
                self.continent,
                self.primary_prefix)

class CTYParser:
    def __init__(self):
        self.current_country = None
        self.parsed = []

    def get_countries(self):
        return self.parsed

    def parse_file(self, the_file):
        for line in the_file:
            self.parse_line(line)

    def parse_line(self, line):
        if line[0] == '#':
            return 0
        if line[0] in [' ', '\t']:
            self.parse_aliases(line)
        else:
            self.parse_country(line)

    def parse_aliases(self, line):
        if self.current_country is None:
            raise RuntimeError('Alias list begun before country')
        line = line.strip()
        close = line[-1] == ';'
        if close:
            line = line[:-1]
        if line[-1] == ',':
            line = line[:-1]
        aliases = line.split(',')
        for alias in aliases:
            self.parse_alias(alias)
        if close:
            self.parsed.append(self.current_country)
            self.current_country = None

    def parse_alias(self, string):
        match = re.match(r'=?([\w\/]+)', string) # TODO: this is not the correct way to handle the = character
        self.current_country.add_alias(match.group(1))

    def parse_country(self, line):
        if self.current_country is not None:
            raise RuntimeError('New country begun before alias list finished')
        name = line[0:26].partition(':')[0]
        cqzone = int(line[26:31].partition(':')[0])
        ituzone = int(line[31:36].partition(':')[0])
        continent = line[36:46].partition(':')[0]
        prefix = line[69:75].partition(':')[0]
        self.current_country = Country(name, cqzone, ituzone, prefix, continent)

def main():
    with open('itu_iso_mapping.txt', 'r') as f:
        Country.iso_resolver.parse_file(f)

    prs = CTYParser()
    prs.parse_file(sys.stdin)

    for country in prs.get_countries():
        print(country)

if __name__ == '__main__':
    main()
