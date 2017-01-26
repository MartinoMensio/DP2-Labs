// footer for page numbers
exports.footer = {
    height: "1cm",
    contents: function(pageNum, numPages) {
        return "<span style='float:right;font-family: \"Segoe WPC\", \"Segoe UI\", \"SFUIText-Light\", \"HelveticaNeue-Light\", sans-serif, \"Droid Sans Fallback\";font-size: 12px;padding-left: 12px;line-height: 22px;text-align: justify;text-justify: inter-word;'>Page " + pageNum + " of " + numPages + "</span>"
    }
}