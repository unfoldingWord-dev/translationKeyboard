require 'test_helper'

class KeyboardConvertorControllerTest < ActionController::TestCase
  test "should get convert" do
    get :convert
    assert_response :success
  end

end
