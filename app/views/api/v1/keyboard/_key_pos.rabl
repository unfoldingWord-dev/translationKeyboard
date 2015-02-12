#

attributes :percent_width
#attributes :row_index, :column_index

child :characters do
    attributes  :modmask
    attributes :unicode_int_value => :unicode
end