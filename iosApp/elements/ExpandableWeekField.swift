import Foundation

import SwiftUI

struct ExpandableWeekField: View {
    let items: [String]
    let label: String
    @Binding var selectedWeekType: String

    @State private var isExpanded = false

    var body: some View {
        Menu {
            ForEach(items, id: \.self) { type in
                Button(action: {
                    selectedWeekType = type
                    isExpanded = false
                }) {
                    Text(type)
                }
            }
        } label: {
            HStack {
                Text(selectedWeekType.isEmpty ? label : selectedWeekType)
                Spacer()
                Image(systemName: "chevron.down")
            }
            .padding()
            .background(Color.gray.opacity(0.1))
            .cornerRadius(8)
        }
    }
}