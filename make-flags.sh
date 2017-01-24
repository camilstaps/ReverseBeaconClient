#!/bin/bash

REPO="https://github.com/maxyz/iso-country-flags-svg-collection/"
DIR="flags"
FLAGS="svg/country-4x3"
BACK="../../.."

declare -A formats=(\
	["l"]="36x27"\
	["m"]="48x36"\
	["h"]="72x54"\
	["xh"]="96x72"\
	["xxh"]="144x108"\
	["xxxh"]="192x144")

cd "$(dirname "$0")"

if [ ! -d "$DIR" ]; then
	git clone "$REPO" "$DIR"
fi

cd "$DIR/$FLAGS"

#git pull

for dens in "${!formats[@]}"; do
	densdir="drawable-${dens}dpi"
	width="${formats[$dens]%x*}"
	height="${formats[$dens]#*x}"
	echo -e "${dens}dpi\t$width x $height"

	mkdir -p "$densdir"
	for f in *.svg; do
		inkscape -z -e "$densdir/flag_${f/.svg/.png}" -w "$width" -h "$height" "$f"
	done
	mv "$densdir/flag__united_nations.png" "$densdir/flag_united_nations.png"
	rm "$densdir"/flag__* "$densdir/flag_aa.png"
	ln -s "flag_united_nations.png" "$densdir/flag_icao.png"
	ln -s "flag_united_nations.png" "$densdir/flag_wmo.png"
done

cd "$BACK"

rsync -lr "$DIR/$FLAGS/"drawable-* app/src/main/res
rm -r "$DIR/$FLAGS/drawable-"*
