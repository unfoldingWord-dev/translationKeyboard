class Api::V3::KeyboardController < Api::V3::BaseController

  api :GET, "api/v3/keyboard", "Get list of available languages"
  def index
    @keyboards = Keyboard.all
    latest_keyboard = Keyboard.order('updated_at').last
    @updated_at_epoch = latest_keyboard.updated_at_epoch
  end


  def show
    @a_keyboard = Keyboard.find(params[:id])
  end

end
