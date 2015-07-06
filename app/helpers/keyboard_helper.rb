module KeyboardHelper

  def intToHtmlHex(unicodeInt)
    hexedInt = unicodeInt.to_s(16)
    if hexedInt == '20'
      "&nbsp;".html_safe
    else
      "&\#x#{hexedInt};".html_safe
    end
  end

end
