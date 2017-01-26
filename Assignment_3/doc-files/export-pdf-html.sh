# requires markdown-to-html (from npm) and markdown-pdf
cp resources_chart.svg ../WebContent/
cp markdown.css ../WebContent/
markdown-pdf --css-path pdf.css -o ../doc/nffgVerifier.pdf --paper-format 'A3' nffgVerifier.md
github-markdown --stylesheet markdown.css --title "NffgService API" nffgVerifier.md > ../WebContent/index.html
