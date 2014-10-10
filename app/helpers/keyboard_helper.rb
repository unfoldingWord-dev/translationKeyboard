module KeyboardHelper

  def intToHtmlHex(unicodeInt)
    hexedInt = unicodeInt.to_s(16)
    "&\#x#{hexedInt};".html_safe
  end

end
